package com.justintime.jit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailedEmail extends BaseEntity {
    private String toEmail;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String body;
    private LocalDateTime failedAt;
    private String reason;
}
