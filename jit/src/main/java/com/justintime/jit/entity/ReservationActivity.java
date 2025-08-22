package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
public class ReservationActivity extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "change_log", nullable = false, length = 50)
    private String changeLog;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

}