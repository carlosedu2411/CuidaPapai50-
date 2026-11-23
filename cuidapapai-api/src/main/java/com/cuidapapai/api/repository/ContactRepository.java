package com.cuidapapai.api.repository;

import com.cuidapapai.api.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    List<Contact> findAllByOrderByNameAsc();
    List<Contact> findByUserIdOrderByNameAsc(String userId);
}
