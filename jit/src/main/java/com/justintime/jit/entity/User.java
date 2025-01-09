package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name", nullable = false)
        private String lastName;

        @Column(name = "profile_picture_url")
        private String profilePictureUrl;

        @Column(name = "is_active", nullable = false)
        private Boolean isActive;

        @Column(name = "user_name", nullable = false)
        private String userName;

        @Column(name = "email", nullable = false)
        private String email;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "password_hash", nullable = false)
        private String passwordHash;

        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        private Role role;

        @CreationTimestamp
        @Column(name = "created_dttm", nullable = false, updatable = false)
        private LocalDateTime createdDttm;

        @UpdateTimestamp
        @Column(name = "updated_dttm", nullable = false)
        private LocalDateTime updatedDttm;

        @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("customer")
        private List<Order> orders;

        @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("customer")
        private List<Reservation> reservations;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("user")
        private List<Admin> admins;

        // Copy Constructor
        public User(User other) {
                this.id = other.id;
                this.firstName = other.firstName;
                this.lastName = other.lastName;
                this.profilePictureUrl = other.profilePictureUrl;
                this.isActive = other.isActive;
                this.userName = other.userName;
                this.email = other.email;
                this.phoneNumber = other.phoneNumber;
                this.passwordHash = other.passwordHash;
                this.role = other.role;
                this.createdDttm = other.createdDttm;
                this.updatedDttm = other.updatedDttm;
                this.orders = other.orders != null ? other.orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Deep copy of orders
                this.reservations = other.reservations != null ? other.reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Deep copy of reservations
                this.admins = other.admins != null ? other.admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Deep copy of admins
        }

        public List<Order> getOrders() {
                return orders != null ? orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Defensive copy
        }

        public void setOrders(List<Order> orders) {
                this.orders = orders != null ? orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Defensive copy
        }

        public List<Reservation> getReservations() {
                return reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
        }

        public void setReservations(List<Reservation> reservations) {
                this.reservations = reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
        }

        public List<Admin> getAdmins() {
                return admins != null ? admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Defensive copy
        }

        public void setAdmins(List<Admin> admins) {
                this.admins = admins != null ? admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Defensive copy
        }
}