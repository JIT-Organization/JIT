package com.justintime.jit.entity;

import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

        @Column(name = "first_name", nullable = false)
        private String firstName;

        @Column(name = "last_name")
        private String lastName;

        @Column(name = "profile_picture_url")
        private String profilePictureUrl;

        @Column(name = "is_active", nullable = false)
        private Boolean isActive;

        @Column(name = "user_name")
        private String username;

        @Column(name = "email", nullable = false)
        private String email;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "password_hash")
        private String passwordHash;

        @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        private Role role;

        @Column(name = "shift")
        private String shift;

        @ManyToMany
        @JoinTable(
                name = "user_permissions",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "permission_id")
        )
        private Set<Permissions> permissions;

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

        @ManyToMany(mappedBy = "cookSet")
        private Set<MenuItem> menuItemSet;

        @OneToMany(mappedBy = "cook")
        private Set<Batch> batches;

        @ManyToMany
        @JoinTable(
                name = "batch_config_cook",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "batch_config_id")
        )
        private Set<BatchConfig> batchConfigs = new HashSet<>();

        @OneToMany(mappedBy = "cook")
        private List<OrderItem> orderItems = new ArrayList<>();


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