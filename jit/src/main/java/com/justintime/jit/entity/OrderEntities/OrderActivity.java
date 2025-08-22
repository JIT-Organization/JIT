package com.justintime.jit.entity.OrderEntities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.justintime.jit.entity.BaseEntity;
import jakarta.persistence.*;
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
@Table(name = "order_activity")
public class OrderActivity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "change_log", nullable = false, length = 50)
    private String changeLog;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;
}