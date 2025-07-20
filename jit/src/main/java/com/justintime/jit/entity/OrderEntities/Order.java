package com.justintime.jit.entity.OrderEntities;

import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.entity.Reservation;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.entity.User;
import com.justintime.jit.util.CodeNumberGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "order_number", unique = true, nullable = false, updatable = false)
    private String orderNumber;

    @PrePersist
    protected void onCreate() {
        this.orderNumber = CodeNumberGenerator.generateCode("order");
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name ="status", nullable = false)
    private OrderStatus status;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name="notes")
    private String notes;

    @Column(name = "serve_time", nullable = false)
    private LocalDateTime serveTime;

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal amount;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderActivity> orderActivities;
}