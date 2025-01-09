package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "shift_capacity")
public class ShiftCapacity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "restaurant_id", nullable = false)
        @JsonIgnoreProperties("shiftCapacities")
        private Restaurant restaurant;

        @Column(name = "start_time", nullable = false)
        private LocalTime startTime;

        @Column(name = "end_time", nullable = false)
        private LocalTime endTime;

        @Column(name = "total_capacity", nullable = false)
        private Integer totalCapacity;

        @CreationTimestamp
        @Column(name = "created_dttm", nullable = false, updatable = false)
        private LocalDateTime createdDttm;

        @UpdateTimestamp
        @Column(name = "updated_dttm", nullable = false)
        private LocalDateTime updatedDttm;

        @OneToMany(mappedBy = "shiftCapacity", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("shiftCapacity")
        private List<Reservation> reservations;

        // Copy Constructor
        public ShiftCapacity(ShiftCapacity other) {
                this.id = other.id;
                this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
                this.startTime = other.startTime;
                this.endTime = other.endTime;
                this.totalCapacity = other.totalCapacity;
                this.createdDttm = other.createdDttm;
                this.updatedDttm = other.updatedDttm;
                this.reservations = other.reservations != null ? other.reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Deep copy of reservations
        }

        public Restaurant getRestaurant() {
                return restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
        }

        public void setRestaurant(Restaurant restaurant) {
                this.restaurant = restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
        }

        public List<Reservation> getReservations() {
                return reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
        }

        public void setReservations(List<Reservation> reservations) {
                this.reservations = reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
        }
}