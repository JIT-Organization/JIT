package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cook")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class Cook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "specialty")
    private String specialty; // e.g., Italian, Indian, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    @JsonIgnoreProperties("cooks")
    private Address address;

    // Many cooks can work for a single restaurant
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnoreProperties("cooks")
    private Restaurant restaurant;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("cook")
    private User user;

    @ManyToMany(mappedBy = "cookSet")
    @JsonIgnoreProperties("cookSet")
    private Set<MenuItem> menuItemSet;
}

