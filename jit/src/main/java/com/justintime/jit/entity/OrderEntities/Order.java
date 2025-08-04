package com.justintime.jit.entity.OrderEntities;

import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.Enums.OrderType;
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
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "order_number", unique = true, updatable = false)
    private String orderNumber;

    @PrePersist
    protected void onCreate() {
        this.orderNumber = CodeNumberGenerator.generateCode("order");
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name ="status", length = 20)
    private OrderStatus status;

    @Column(name = "order_type")
    private OrderType orderType;

    @Column(name = "order_date", updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name="notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "serve_time")
    private LocalDateTime serveTime;

    @Column(name = "amount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal amount;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<Payment> payments;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderActivity> orderActivities;

}