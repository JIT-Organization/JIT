package com.justintime.jit.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CookLoadDelta extends BaseEntity {
    private Long cookId;
    private Integer deltaMinutes;
    private Long orderItemId;
    private Timestamp createdAt;
    private Boolean processed;
}
