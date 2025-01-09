package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "reservation_activity")
public class ReservationActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    @JsonIgnoreProperties("reservationActivities")
    private Reservation reservation;

    @Column(name = "change_log", nullable = false, length = 50)
    private String changeLog;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    // Copy Constructor
    public ReservationActivity(ReservationActivity other) {
        this.id = other.id;
        this.reservation = other.reservation != null ? new Reservation(other.reservation) : null;
        this.changeLog = other.changeLog;
        this.updatedBy = other.updatedBy;
        this.updatedDttm = other.updatedDttm;
    }

    public Reservation getReservation() {
        return reservation != null ? new Reservation(reservation) : null; // Defensive copy
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation != null ? new Reservation(reservation) : null; // Defensive copy
    }
}