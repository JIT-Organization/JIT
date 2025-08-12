package com.justintime.jit.entity;

import com.justintime.jit.entity.Enums.ConfigurationName;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class BusinessConfiguration extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ConfigurationName configurationName;
    private String value;
    private String restaurantCode;
}
