package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity{

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name", nullable = false)
        private String lastName;

        @Column(name = "profile_picture_url")
        private String profilePictureUrl;

        @Column(name = "is_active", nullable = false)
        private Boolean isActive;

        @Column(name = "user_name", nullable = false)
        private String username;

        @Column(name = "email", nullable = false)
        private String email;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "password_hash", nullable = false)
        private String passwordHash;

        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        private Role role;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private List<Order> orders;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
        private List<Reservation> reservations;

        @ManyToMany
        @JoinTable(
                name = "user_restaurant",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "restaurant_id")
        )
        private Set<Restaurant> restaurants;

//        // Copy Constructor
//        public User(User other) {
//                this.id = other.id;
//                this.firstName = other.firstName;
//                this.lastName = other.lastName;
//                this.profilePictureUrl = other.profilePictureUrl;
//                this.isActive = other.isActive;
//                this.userName = other.userName;
//                this.email = other.email;
//                this.phoneNumber = other.phoneNumber;
//                this.passwordHash = other.passwordHash;
//                this.role = other.role;
//                this.createdDttm = other.createdDttm;
//                this.updatedDttm = other.updatedDttm;
//                this.orders = other.orders != null ? other.orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Deep copy of orders
//                this.reservations = other.reservations != null ? other.reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Deep copy of reservations
//                this.admins = other.admins != null ? other.admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Deep copy of admins
//        }
//
//        public List<Order> getOrders() {
//                return orders != null ? orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setOrders(List<Order> orders) {
//                this.orders = orders != null ? orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public List<Reservation> getReservations() {
//                return reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setReservations(List<Reservation> reservations) {
//                this.reservations = reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public List<Admin> getAdmins() {
//                return admins != null ? admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setAdmins(List<Admin> admins) {
//                this.admins = admins != null ? admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
}