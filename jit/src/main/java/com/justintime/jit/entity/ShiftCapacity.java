package com.justintime.jit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalTime;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "shift_capacity")
public class ShiftCapacity extends BaseEntity {

        @ManyToOne
        @JoinColumn(name = "restaurant_id", nullable = false)
        private Restaurant restaurant;

        @Column(name = "start_time", nullable = false)
        private LocalTime startTime;

        @Column(name = "end_time", nullable = false)
        private LocalTime endTime;

        @Column(name = "total_capacity", nullable = false)
        private Integer totalCapacity;

        @OneToMany(mappedBy = "shiftCapacity", cascade = CascadeType.ALL)
        private List<Reservation> reservations;

//        // Copy Constructor
//        public ShiftCapacity(ShiftCapacity other) {
//                this.id = other.id;
//                this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
//                this.startTime = other.startTime;
//                this.endTime = other.endTime;
//                this.totalCapacity = other.totalCapacity;
//                this.createdDttm = other.createdDttm;
//                this.updatedDttm = other.updatedDttm;
//                this.reservations = other.reservations != null ? other.reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Deep copy of reservations
//        }
//
//        public Restaurant getRestaurant() {
//                return restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
//        }
//
//        public void setRestaurant(Restaurant restaurant) {
//                this.restaurant = restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
//        }
//
//        public List<Reservation> getReservations() {
//                return reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setReservations(List<Reservation> reservations) {
//                this.reservations = reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
}