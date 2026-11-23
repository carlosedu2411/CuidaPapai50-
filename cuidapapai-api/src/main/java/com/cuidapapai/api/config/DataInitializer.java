package com.cuidapapai.api.config;

import com.cuidapapai.api.model.*;
import com.cuidapapai.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MedicationRepository medicationRepository;
    private final AppointmentRepository appointmentRepository;
    private final CareTaskRepository careTaskRepository;
    private final ContactRepository contactRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Inicializando dados de teste da aplicação CuidaPapai...");

            String userId = "usr_987654";

            // 1. Perfil do Usuário
            User user = User.builder()
                    .id(userId)
                    .name("Maria da Silva")
                    .shortName("Dona Maria")
                    .role("elderly")
                    .theme("light")
                    .fontSize("large")
                    .build();
            userRepository.save(user);

            // 2. Medicamentos de Hoje
            LocalDate today = LocalDate.now();
            medicationRepository.save(Medication.builder()
                    .id("med_001")
                    .userId(userId)
                    .name("Losartana")
                    .dosage("50mg")
                    .scheduledTime("09:00")
                    .instructions("Após o café da manhã")
                    .isTaken(true)
                    .takenAt(OffsetDateTime.now(ZoneOffset.UTC).minusHours(3))
                    .scheduledDate(today)
                    .build());

            medicationRepository.save(Medication.builder()
                    .id("med_002")
                    .userId(userId)
                    .name("Metformina")
                    .dosage("850mg")
                    .scheduledTime("13:00")
                    .instructions("Durante o almoço")
                    .isTaken(false)
                    .takenAt(null)
                    .scheduledDate(today)
                    .build());

            // 3. Agenda de Compromissos
            appointmentRepository.save(Appointment.builder()
                    .id("apt_101")
                    .userId(userId)
                    .eventDatetime(OffsetDateTime.now(ZoneOffset.UTC).plusDays(2).withHour(10).withMinute(30).withSecond(0).withNano(0))
                    .title("Consulta com cardiologista")
                    .subtitle("Clínica São Lucas - Dr. Roberto")
                    .colorHex("#000080")
                    .build());

            appointmentRepository.save(Appointment.builder()
                    .id("apt_102")
                    .userId(userId)
                    .eventDatetime(OffsetDateTime.now(ZoneOffset.UTC).plusDays(4).withHour(14).withMinute(0).withSecond(0).withNano(0))
                    .title("Exame de Rotina")
                    .subtitle("Laboratório São Paulo")
                    .colorHex("#126E51")
                    .build());

            // 4. Cuidados Diários
            careTaskRepository.save(CareTask.builder()
                    .id("task_01")
                    .userId(userId)
                    .category("blood_pressure")
                    .title("Pressão arterial")
                    .subtitle("Registrado hoje (120x80)")
                    .isCompleted(true)
                    .value("120x80")
                    .completedAt(OffsetDateTime.now(ZoneOffset.UTC).minusHours(4))
                    .colorHex("#126E51")
                    .taskDate(today)
                    .build());

            careTaskRepository.save(CareTask.builder()
                    .id("task_02")
                    .userId(userId)
                    .category("hydration")
                    .title("Hidratação (Água)")
                    .subtitle("Pendente para a tarde")
                    .isCompleted(false)
                    .value(null)
                    .completedAt(null)
                    .colorHex("#000080")
                    .taskDate(today)
                    .build());

            // 5. Rede de Apoio
            contactRepository.save(Contact.builder()
                    .id("cont_001")
                    .userId(userId)
                    .name("Mariana Costa")
                    .relation("Filha · Administradora")
                    .initials("MC")
                    .isProfessional(false)
                    .isAvailableToday(true)
                    .phone("+5511999999999")
                    .avatarColorHex("#126E51")
                    .build());

            contactRepository.save(Contact.builder()
                    .id("cont_002")
                    .userId(userId)
                    .name("Dr. Roberto Silva")
                    .relation("Cardiologista")
                    .initials("RS")
                    .isProfessional(true)
                    .isAvailableToday(false)
                    .phone("+5511988887777")
                    .avatarColorHex("#000080")
                    .build());

            log.info("Dados de teste criados com sucesso!");
        }
    }
}
