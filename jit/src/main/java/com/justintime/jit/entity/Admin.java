package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnoreProperties("admins")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("admins")
    private User user;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    // Copy Constructor
    public Admin(Admin other) {
        this.id = other.id;
        this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
        this.user = other.user != null ? new User(other.user) : null;
        this.createdDttm = other.createdDttm;
        this.updatedDttm = other.updatedDttm;
    }

    public Restaurant getRestaurant() {
        return restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
    }

    public User getUser() {
        return user != null ? new User(user) : null; // Defensive copy
    }

    public void setUser(User user) {
        this.user = user != null ? new User(user) : null; // Defensive copy
    }
}