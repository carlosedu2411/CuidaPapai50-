package com.cuidapapai.api.repository;

import com.cuidapapai.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findFirstByOrderByIdAsc();
}
