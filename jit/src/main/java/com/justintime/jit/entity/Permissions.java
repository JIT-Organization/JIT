package com.justintime.jit.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "permission_code", unique = true, nullable = false)
    private String permissionCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

}