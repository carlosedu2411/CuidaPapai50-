package com.cuidapapai.api.service;

import com.cuidapapai.api.dto.ContactResponse;
import com.cuidapapai.api.model.Contact;
import com.cuidapapai.api.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public List<ContactResponse> getContacts() {
        return contactRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
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
