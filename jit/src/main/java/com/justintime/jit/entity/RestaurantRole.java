package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.justintime.jit.entity.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_roles")
public class RestaurantRole extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private Role roleType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "permission_codes", columnDefinition = "TEXT")
    private String permissionCodes;

    @OneToMany(mappedBy = "restaurantRole", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @Column(name = "restaurant_code", nullable = false)
    private String restaurantCode;
}
