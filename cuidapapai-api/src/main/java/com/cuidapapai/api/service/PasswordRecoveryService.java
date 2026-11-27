package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.PasswordRecoveryRequest;
import com.cuidapapai.api.dto.PasswordResetRequest;
import com.cuidapapai.api.model.PasswordRecoveryCode;
import com.cuidapapai.api.model.User;
import com.cuidapapai.api.repository.PasswordRecoveryCodeRepository;
import com.cuidapapai.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Logger LOGGER = Logger.getLogger(PasswordRecoveryService.class.getName());
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();
    private static final URI SENDGRID_MAIL_URI = URI.create("https://api.sendgrid.com/v3/mail/send");

    private final UserRepository userRepository;
    private final PasswordRecoveryCodeRepository recoveryCodeRepository;

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from}")
    private String fromAddress;

    public void sendRecoveryCode(PasswordRecoveryRequest request) {
        String email = request.email().trim();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }
        if (sendGridApiKey == null || sendGridApiKey.isBlank()
                || fromAddress == null || fromAddress.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Configure SENDGRID_API_KEY e SENDGRID_FROM para enviar e-mails."
            );
        }

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        PasswordRecoveryCode recoveryCode = PasswordRecoveryCode.builder()
                .email(email)
                .codeHash(hashCode(code))
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .failedAttempts(0)
                .build();
        recoveryCodeRepository.save(recoveryCode);

        String plainText = "Seu código de recuperação é " + code
                + ". Ele expira em 10 minutos e só pode ser usado uma vez.";
        String htmlContent = buildRecoveryEmailHtml(code);

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(SENDGRID_MAIL_URI)
                    .timeout(java.time.Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + sendGridApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            buildSendGridPayload(email, plainText, htmlContent),
                            StandardCharsets.UTF_8
                    ))
                    .build();
            HttpResponse<String> response = HTTP_CLIENT.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            if (response.statusCode() != 202) {
                recoveryCodeRepository.deleteById(email);
                LOGGER.warning("SendGrid Mail Send API returned HTTP " + response.statusCode());
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "O SendGrid não aceitou o envio do e-mail de recuperação."
                );
            }
        } catch (IOException exception) {
            recoveryCodeRepository.deleteById(email);
            LOGGER.log(Level.SEVERE, "Could not reach SendGrid Mail Send API.", exception);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível enviar o e-mail de recuperação.",
                    exception
            );
        } catch (InterruptedException exception) {
            recoveryCodeRepository.deleteById(email);
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "O envio do e-mail de recuperação foi interrompido.",
                    exception
            );
        }
    }

    private String buildSendGridPayload(String email, String plainText, String htmlContent) {
        return "{\"personalizations\":[{\"to\":[{\"email\":\"" + escapeJson(email)
                + "\"}]}],\"from\":{\"email\":\"" + escapeJson(fromAddress)
                + "\"},\"subject\":\"" + escapeJson("Código para recuperar sua senha - CuidaPapai")
                + "\",\"content\":[{\"type\":\"text/plain\",\"value\":\"" + escapeJson(plainText)
                + "\"},{\"type\":\"text/html\",\"value\":\"" + escapeJson(htmlContent) + "\"}]}";
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void resetPassword(PasswordResetRequest request) {
        String email = request.email().trim();
        PasswordRecoveryCode recoveryCode = recoveryCodeRepository.findById(email)
                .orElseThrow(PasswordRecoveryService::invalidCode);

        if (!recoveryCode.getExpiresAt().isAfter(Instant.now())) {
            recoveryCodeRepository.delete(recoveryCode);
            throw invalidCode();
        }

        if (recoveryCode.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            recoveryCodeRepository.delete(recoveryCode);
            throw invalidCode();
        }

        byte[] expectedHash = recoveryCode.getCodeHash().getBytes(StandardCharsets.UTF_8);
        byte[] providedHash = hashCode(request.code()).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expectedHash, providedHash)) {
            recoveryCode.setFailedAttempts(recoveryCode.getFailedAttempts() + 1);
            if (recoveryCode.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                recoveryCodeRepository.delete(recoveryCode);
            } else {
                recoveryCodeRepository.save(recoveryCode);
            }
            throw invalidCode();
        }

        User user = userRepository.findByEmail(email).orElseThrow(PasswordRecoveryService::invalidCode);
        user.setPassword(request.newPassword());
        userRepository.save(user);
        recoveryCodeRepository.delete(recoveryCode);
    }

    private static String buildRecoveryEmailHtml(String code) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>Código de Recuperação - CuidaPapai</title>
                </head>
                <body style="margin:0;padding:0;background-color:#F4F7F2;font-family:Arial,Helvetica,sans-serif;">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color:#F4F7F2;padding:32px 16px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:520px;background-color:#FFFFFF;border-radius:16px;overflow:hidden;border:2px solid #2E7D32;">
                          <tr>
                            <td style="background-color:#1B5E20;padding:24px 28px;border-bottom:5px solid #FBC02D;">
                              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                <tr>
                                  <td>
                                    <span style="display:inline-block;width:10px;height:10px;border-radius:50%%;background-color:#D32F2F;margin-right:8px;vertical-align:middle;"></span>
                                    <span style="color:#FFF59D;font-size:12px;font-weight:bold;letter-spacing:1.2px;text-transform:uppercase;vertical-align:middle;">Segurança da Conta</span>
                                    <h1 style="margin:8px 0 0 0;color:#FFFFFF;font-size:24px;font-weight:bold;">CuidaPapai</h1>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:28px;">
                              <h2 style="margin:0 0 12px 0;color:#1B5E20;font-size:20px;">Código para recuperar sua senha</h2>
                              <p style="margin:0 0 22px 0;color:#374151;font-size:15px;line-height:1.6;">
                                Olá! Recebemos uma solicitação para redefinir a senha da sua conta. Use o código abaixo no aplicativo:
                              </p>

                              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin:0 0 20px 0;">
                                <tr>
                                  <td align="center" style="background-color:#FFF8E1;border:2px solid #FBC02D;border-radius:12px;padding:20px 16px;">
                                    <div style="font-size:12px;font-weight:bold;color:#2E7D32;text-transform:uppercase;letter-spacing:1px;margin-bottom:8px;">Seu código de verificação</div>
                                    <div style="font-size:34px;font-weight:bold;letter-spacing:8px;color:#1B5E20;font-family:'Courier New',Courier,monospace;">%s</div>
                                  </td>
                                </tr>
                              </table>

                              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin:0 0 20px 0;">
                                <tr>
                                  <td style="background-color:#E8F5E9;border-left:4px solid #D32F2F;border-radius:6px;padding:12px 14px;">
                                    <p style="margin:0;color:#1B5E20;font-size:13px;line-height:1.5;">
                                      <strong style="color:#C62828;">Atenção:</strong> este código <strong>expira em 10 minutos</strong> e só pode ser usado <strong>uma única vez</strong>.
                                    </p>
                                  </td>
                                </tr>
                              </table>

                              <p style="margin:0;color:#6B7280;font-size:13px;line-height:1.5;">
                                Se você não solicitou a troca de senha, pode ignorar este e-mail com segurança.
                                <span style="color:#C62828;font-weight:bold;">Nunca compartilhe este código</span> com outras pessoas.
                              </p>
                            </td>
                          </tr>
                          <tr>
                            <td style="background-color:#F1F8E9;padding:16px 28px;border-top:2px solid #FBC02D;text-align:center;">
                              <p style="margin:0;color:#2E7D32;font-size:12px;font-weight:bold;">
                                CuidaPapai <span style="color:#D32F2F;">&#9679;</span> Cuidado e rotina com carinho
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(code);
    }

    private static String hashCode(String code) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(code.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }

    private static ResponseStatusException invalidCode() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código inválido ou expirado.");
    }
}
