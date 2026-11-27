package com.cuidapapai.api.repository;

import com.cuidapapai.api.model.PasswordRecoveryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRecoveryCodeRepository extends JpaRepository<PasswordRecoveryCode, String> {
}
