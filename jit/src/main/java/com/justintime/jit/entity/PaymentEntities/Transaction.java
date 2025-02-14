package com.justintime.jit.entity.PaymentEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.justintime.jit.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transactionAmount;

    @Column(name = "transaction_status", nullable = false, length = 50)
    private String transactionStatus;

    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

//    public Transaction(Transaction other) {
//        this.id = null; // New instance should not have the same ID
//        this.payment = other.payment != null ? new Payment(other.payment) : null; // Deep copy of Payment
//        this.transactionType = other.transactionType;
//        this.transactionAmount = other.transactionAmount;
//        this.transactionStatus = other.transactionStatus;
//        this.transactionDate = other.transactionDate;
//        this.updatedBy = other.updatedBy;
//        this.updatedDttm = other.updatedDttm;
//    }
//
//    public Payment getPayment() {
//        return payment != null ? new Payment(payment) : null; // Defensive copy
//    }
//
//    public void setPayment(Payment payment) {
//        this.payment = payment != null ? new Payment(payment) : null; // Defensive copy
//    }
}