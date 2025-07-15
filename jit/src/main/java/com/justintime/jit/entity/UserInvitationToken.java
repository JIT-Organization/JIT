package com.justintime.jit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_invitation_token")
public class UserInvitationToken extends BaseEntity {
    private String token;

    private String email;

    private LocalDateTime expiresAt;

    private boolean used;

    @OneToOne
    private User user;
}
