package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.ContactResponse;
import com.cuidapapai.api.dto.CreateContactRequest;
import com.cuidapapai.api.model.Contact;
import com.cuidapapai.api.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public List<ContactResponse> getContacts(String userId) {
        List<Contact> contacts;
        if (userId != null && !userId.isBlank()) {
            contacts = contactRepository.findByUserIdOrderByNameAsc(userId);
        } else {
            contacts = contactRepository.findAllByOrderByNameAsc();
        }

        return contacts.stream()
                .map(this::toResponse)
                .toList();
    }

    public ContactResponse createContact(String userId, CreateContactRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do contato é obrigatório.");
        }
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O telefone é obrigatório.");
        }

        String contactId = "cnt_" + UUID.randomUUID().toString().substring(0, 8);
        String initials = request.getInitials() != null && !request.getInitials().isBlank()
                ? request.getInitials()
                : request.getName().substring(0, Math.min(2, request.getName().length())).toUpperCase();
        String relation = request.getRelation() != null && !request.getRelation().isBlank()
                ? request.getRelation()
                : "Familiar";
        String avatarColor = request.getAvatarColorHex() != null && !request.getAvatarColorHex().isBlank()
                ? request.getAvatarColorHex()
                : "#6B7280";

        Contact contact = Contact.builder()
                .id(contactId)
                .userId(userId)
                .name(request.getName().trim())
                .relation(relation)
                .initials(initials)
                .isProfessional(request.getIsProfessional() != null ? request.getIsProfessional() : false)
                .isAvailableToday(request.getIsAvailableToday())
                .phone(request.getPhone().trim())
                .avatarColorHex(avatarColor)
                .build();

        Contact saved = contactRepository.save(contact);
        return toResponse(saved);
    }

    private ContactResponse toResponse(Contact c) {
        return ContactResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .relation(c.getRelation())
                .initials(c.getInitials())
                .isProfessional(Boolean.TRUE.equals(c.getIsProfessional()))
                .phone(c.getPhone())
                .avatarColorHex(c.getAvatarColorHex())
                .build();
    }
}
