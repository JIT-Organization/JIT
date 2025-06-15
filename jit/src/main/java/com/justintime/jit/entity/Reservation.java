package com.justintime.jit.entity;

import com.justintime.jit.util.CodeNumberGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name="reservation")
public class Reservation extends BaseEntity{

        @Column(unique = true, nullable = false, updatable = false)
        private String reservationNumber;

        @PrePersist
        protected void onCreate() {
                this.reservationNumber = CodeNumberGenerator.generateCode("reservation");
        }

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToOne
        @JoinColumn(name = "restaurant_id", nullable = false)
        private Restaurant restaurant;

        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(
                name = "dining_table_reservation",
                joinColumns = @JoinColumn(name = "reservation_id"),
                inverseJoinColumns = @JoinColumn(name = "dining_table_id"),
                uniqueConstraints = @UniqueConstraint(columnNames = {"reservation_id", "dining_table_id"})
        )
        private Set<DiningTable> diningTableSet = new HashSet<>();

        @ManyToOne
        @JoinColumn(name = "shift_capacity_id", nullable = false)
        private ShiftCapacity shiftCapacity;

        @Column(name = "reservation_start", nullable = false)
        private LocalDateTime reservationStart;

        @Column(name = "reservation_end", nullable = false)
        private LocalDateTime reservationEnd;

        @Column(name = "head_count", nullable = false)
        private Integer headCount;

        @Column(name = "status", nullable = false, length = 50)
        private String status;

        @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
        private List<ReservationActivity> reservationActivities;

//        // Copy Constructor
//        public Reservation(Reservation other) {
//                this.id = other.id;
//                this.customer = other.customer != null ? new User(other.customer) : null;
//                this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
//                this.shiftCapacity = other.shiftCapacity != null ? new ShiftCapacity(other.shiftCapacity) : null;
//                this.reservationStart = other.reservationStart;
//                this.reservationEnd = other.reservationEnd;
//                this.headCount = other.headCount;
//                this.status = other.status;
//                this.createdDttm = other.createdDttm;
//                this.updatedDttm = other.updatedDttm;
//                this.reservationActivities = other.reservationActivities != null ? other.reservationActivities.stream().map(ReservationActivity::new).collect(Collectors.toList()) : null; // Deep copy of reservationActivities
//        }
//
//        public User getCustomer() {
//                return customer != null ? new User(customer) : null; // Defensive copy
//        }
//
//        public void setCustomer(User customer) {
//                this.customer = customer != null ? new User(customer) : null; // Defensive copy
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
//        public ShiftCapacity getShiftCapacity() {
//                return shiftCapacity != null ? new ShiftCapacity(shiftCapacity) : null; // Defensive copy
//        }
//
//        public void setShiftCapacity(ShiftCapacity shiftCapacity) {
//                this.shiftCapacity = shiftCapacity != null ? new ShiftCapacity(shiftCapacity) : null; // Defensive copy
//        }
//
//        public List<ReservationActivity> getReservationActivities() {
//                return reservationActivities != null ? reservationActivities.stream().map(ReservationActivity::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setReservationActivities(List<ReservationActivity> reservationActivities) {
//                this.reservationActivities = reservationActivities != null ? reservationActivities.stream().map(ReservationActivity::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
}