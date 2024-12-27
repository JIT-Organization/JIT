package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.justintime.jit.entity.OrderEntities.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Audited
@Data
@Table(name = "restaurants")
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "restaurant_name", nullable = false)
        private String restaurantName;

        @Column(name = "contact_number")
        private String contactNumber;

        @Column(name = "email")
        private String email;

        @CreationTimestamp
        @Column(name = "created_dttm", nullable = false, updatable = false)
        private LocalDateTime createdDttm;

        @UpdateTimestamp
        @Column(name = "updated_dttm", nullable = false)
        private LocalDateTime updatedDttm;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("restaurant")
        private List<Address> addresses;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("restaurant")
        private List<MenuItem> menu;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("restaurant")
        private List<Order> orders;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("restaurant")
        private List<ShiftCapacity> shiftCapacities;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("restaurant")
        private List<Reservation> reservations;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("restaurant")
        private List<Admin> admins;

}
