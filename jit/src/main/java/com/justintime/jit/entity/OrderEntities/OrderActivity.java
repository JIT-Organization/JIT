package com.justintime.jit.entity.OrderEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class OrderActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("orderActivities")
    private Order order;

    @Column(name = "change_log", nullable = false, length = 50)
    private String changeLog;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    // Copy constructor
    public OrderActivity(OrderActivity other) {
        this.id = other.id;
        this.order = other.order != null ? new Order(other.order) : null;
        this.changeLog = other.changeLog;
        this.updatedBy = other.updatedBy;
        this.updatedDttm = other.updatedDttm;
    }

    public Order getOrder() {
        return order != null ? new Order(order) : null; // Defensive copy
    }

    public void setOrder(Order order) {
        this.order = order != null ? new Order(order) : null; // Defensive copy
    }
}