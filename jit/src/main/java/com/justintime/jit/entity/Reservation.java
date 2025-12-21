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
}