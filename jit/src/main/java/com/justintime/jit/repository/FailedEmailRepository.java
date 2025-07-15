package com.justintime.jit.repository;

import com.justintime.jit.entity.FailedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedEmailRepository extends JpaRepository<FailedEmail, Long> {
}
