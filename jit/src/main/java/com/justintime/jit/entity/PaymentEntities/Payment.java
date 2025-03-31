package com.justintime.jit.entity.PaymentEntities;

import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.Enums.PaymentStatus;
import com.justintime.jit.entity.OrderEntities.Order;
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

@Entity
@Audited
@Getter
@Setter
@Table(name = "payment")
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "payment_number", unique = true, nullable = false, updatable = false)
    private String paymentNumber;

    @PrePersist
    protected void onCreate(){
        this.paymentNumber = "PY-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency = "USD";

    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_date", nullable = false, updatable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

//    // Copy Constructor
//    public Payment(Payment other) {
//        this.id = null; // New instance should not have the same ID
//        this.order = other.order != null ? new Order(other.order) : null; // Deep copy of Order
//        this.paymentMethod = other.paymentMethod;
//        this.amount = other.amount;
//        this.currency = other.currency;
//        this.paymentStatus = other.paymentStatus;
//        this.paymentDate = other.paymentDate;
//        this.updatedBy = other.updatedBy;
//        this.updatedDttm = other.updatedDttm;
//        this.transactions = other.transactions != null ? other.transactions.stream().map(Transaction::new).collect(Collectors.toList()) : null; // Deep copy of transactions
//    }
//
//    public Order getOrder() {
//        return order != null ? new Order(order) : null; // Defensive copy
//    }
//
//    public void setOrder(Order order) {
//        this.order = order != null ? new Order(order) : null; // Defensive copy
//    }
//
//    public List<Transaction> getTransactions() {
//        return transactions != null ? transactions.stream().map(Transaction::new).collect(Collectors.toList()) : null; // Defensive copy
//    }
//
//    public void setTransactions(List<Transaction> transactions) {
//        this.transactions = transactions != null ? transactions.stream().map(Transaction::new).collect(Collectors.toList()) : null; // Defensive copy
//    }
}