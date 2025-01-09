package com.justintime.jit.entity.OrderEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("orders")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnoreProperties("orders")
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name ="status", nullable = false)
    private OrderStatus status;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "amount", nullable = false, columnDefinition = "decimal(10,2) default 0.00")
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("order")
    private List<Payment> payments;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("order")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("order")
    private List<OrderActivity> orderActivities;

    // Copy constructor
    public Order(Order other) {
        this.id = other.id;
        this.customer = other.customer != null ? new User(other.customer) : null;
        this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
        this.status = other.status;
        this.orderDate = other.orderDate;
        this.amount = other.amount;
        this.createdDttm = other.createdDttm;
        this.updatedDttm = other.updatedDttm;
        this.payments = other.payments != null ? other.payments.stream().map(Payment::new).collect(Collectors.toList()) : null;
        this.orderItems = other.orderItems != null ? other.orderItems.stream().map(OrderItem::new).collect(Collectors.toList()) : null;
        this.orderActivities = other.orderActivities != null ? other.orderActivities.stream().map(OrderActivity::new).collect(Collectors.toList()) : null;
    }

    public User getCustomer() {
        return customer != null ? new User(customer) : null; // Defensive copy
    }

    public void setCustomer(User customer) {
        this.customer = customer != null ? new User(customer) : null; // Defensive copy
    }

    public Restaurant getRestaurant() {
        return restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
    }

    public List<Payment> getPayments() {
        return payments != null ? payments.stream().map(Payment::new).collect(Collectors.toList()) : null; // Defensive copy
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments != null ? payments.stream().map(Payment::new).collect(Collectors.toList()) : null; // Defensive copy
    }

    public List<OrderItem> getOrderItems() {
        return orderItems != null ? orderItems.stream().map(OrderItem::new).collect(Collectors.toList()) : null; // Defensive copy
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems.stream().map(OrderItem::new).collect(Collectors.toList()) : null; // Defensive copy
    }

    public List<OrderActivity> getOrderActivities() {
        return orderActivities != null ? orderActivities.stream().map(OrderActivity::new).collect(Collectors.toList()) : null; // Defensive copy
    }

    public void setOrderActivities(List<OrderActivity> orderActivities) {
        this.orderActivities = orderActivities != null ? orderActivities.stream().map(OrderActivity::new).collect(Collectors.toList()) : null; // Defensive copy
    }
}