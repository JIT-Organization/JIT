package com.justintime.jit.repository;

import com.justintime.jit.entity.User;
import com.justintime.jit.entity.UserInvitationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInvitationRepository extends JpaRepository<UserInvitationToken, Long> {
    Optional<UserInvitationToken> findByToken(String token);
}
