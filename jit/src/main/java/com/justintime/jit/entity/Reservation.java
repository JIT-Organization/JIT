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
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name="reservation")
public class Reservation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "customer_id", nullable = false)
        @JsonIgnoreProperties("reservations")
        private User customer;

        @ManyToOne
        @JoinColumn(name = "restaurant_id", nullable = false)
        @JsonIgnoreProperties("reservations")
        private Restaurant restaurant;

        @ManyToOne
        @JoinColumn(name = "shift_capacity_id", nullable = false)
        @JsonIgnoreProperties("reservations")
        private ShiftCapacity shiftCapacity;

        @Column(name = "reservation_start", nullable = false)
        private LocalDateTime reservationStart;

        @Column(name = "reservation_end", nullable = false)
        private LocalDateTime reservationEnd;

        @Column(name = "head_count", nullable = false)
        private Integer headCount;

        @Column(name = "status", nullable = false, length = 50)
        private String status;

        @CreationTimestamp
        @Column(name = "created_dttm", nullable = false, updatable = false)
        private LocalDateTime createdDttm;

        @UpdateTimestamp
        @Column(name = "updated_dttm", nullable = false)
        private LocalDateTime updatedDttm;

        @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
        @JsonIgnoreProperties("reservation")
        private List<ReservationActivity> reservationActivities;

        // Copy Constructor
        public Reservation(Reservation other) {
                this.id = other.id;
                this.customer = other.customer != null ? new User(other.customer) : null;
                this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
                this.shiftCapacity = other.shiftCapacity != null ? new ShiftCapacity(other.shiftCapacity) : null;
                this.reservationStart = other.reservationStart;
                this.reservationEnd = other.reservationEnd;
                this.headCount = other.headCount;
                this.status = other.status;
                this.createdDttm = other.createdDttm;
                this.updatedDttm = other.updatedDttm;
                this.reservationActivities = other.reservationActivities != null ? other.reservationActivities.stream().map(ReservationActivity::new).collect(Collectors.toList()) : null; // Deep copy of reservationActivities
        }

        public User getCustomer() {
                return customer != null ? new User(customer) : null; // Defensive copy
        }

        public void setCustomer(User customer) {
                this.customer = customer != null ? new User(customer) : null; // Defensive copy
        }

        public Restaurant getRestaurant() {
                return restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
        }

        public void setRestaurant(Restaurant restaurant) {
                this.restaurant = restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
        }

        public ShiftCapacity getShiftCapacity() {
                return shiftCapacity != null ? new ShiftCapacity(shiftCapacity) : null; // Defensive copy
        }

        public void setShiftCapacity(ShiftCapacity shiftCapacity) {
                this.shiftCapacity = shiftCapacity != null ? new ShiftCapacity(shiftCapacity) : null; // Defensive copy
        }

        public List<ReservationActivity> getReservationActivities() {
                return reservationActivities != null ? reservationActivities.stream().map(ReservationActivity::new).collect(Collectors.toList()) : null; // Defensive copy
        }

        public void setReservationActivities(List<ReservationActivity> reservationActivities) {
                this.reservationActivities = reservationActivities != null ? reservationActivities.stream().map(ReservationActivity::new).collect(Collectors.toList()) : null; // Defensive copy
        }
}