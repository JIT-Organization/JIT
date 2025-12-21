package com.justintime.jit.entity.PaymentEntities;

import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.Enums.PaymentStatus;
import com.justintime.jit.entity.OrderEntities.Order;
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
@Table(name = "payment")
@NoArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "payment_number", unique = true, nullable = false, updatable = false)
    private String paymentNumber;

    @PrePersist
    protected void onCreate(){
        this.paymentNumber = CodeNumberGenerator.generateCode("payment");
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
}