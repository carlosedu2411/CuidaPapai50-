package com.cuidapapai.api.controller;

import com.cuidapapai.api.config.UserResolver;
import com.cuidapapai.api.dto.ContactResponse;
import com.cuidapapai.api.dto.CreateContactRequest;
import com.cuidapapai.api.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/network")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final UserResolver userResolver;

    @GetMapping("/contacts")
    public ResponseEntity<List<ContactResponse>> getContacts(HttpServletRequest request) {
        String userId = userResolver.resolveUserId(request);
        return ResponseEntity.ok(contactService.getContacts(userId));
    }

    @PostMapping("/contacts")
    public ResponseEntity<ContactResponse> createContact(
            HttpServletRequest request,
            @RequestBody CreateContactRequest body
    ) {
        String userId = userResolver.resolveUserId(request);
        ContactResponse created = contactService.createContact(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
