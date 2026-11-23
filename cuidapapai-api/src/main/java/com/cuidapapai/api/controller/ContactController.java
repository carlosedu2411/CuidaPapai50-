package com.cuidapapai.api.controller;

import com.cuidapapai.api.dto.ContactResponse;
import com.cuidapapai.api.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/network")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/contacts")
    public ResponseEntity<List<ContactResponse>> getContacts() {
        return ResponseEntity.ok(contactService.getContacts());
    }
}
