package com.justintime.jit.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.justintime.jit.entity.Enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "permissions")
public class Permissions extends BaseEntity {

    @Access(AccessType.FIELD)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @JsonIgnore
    private Role role;

    @Column(name = "permission_code", unique = true, nullable = false)
    private String permissionCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

}