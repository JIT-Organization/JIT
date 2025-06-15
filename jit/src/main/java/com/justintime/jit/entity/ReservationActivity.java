package com.justintime.jit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reservation_activity")
public class ReservationActivity extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "change_log", nullable = false, length = 50)
    private String changeLog;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

//    // Copy Constructor
//    public ReservationActivity(ReservationActivity other) {
//        this.id = other.id;
//        this.reservation = other.reservation != null ? new Reservation(other.reservation) : null;
//        this.changeLog = other.changeLog;
//        this.updatedBy = other.updatedBy;
//        this.updatedDttm = other.updatedDttm;
//    }
//
//    public Reservation getReservation() {
//        return reservation != null ? new Reservation(reservation) : null; // Defensive copy
//    }
//
//    public void setReservation(Reservation reservation) {
//        this.reservation = reservation != null ? new Reservation(reservation) : null; // Defensive copy
//    }
}