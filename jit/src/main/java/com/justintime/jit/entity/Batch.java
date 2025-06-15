package com.justintime.jit.entity;

import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.Enums.BatchStatus;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.TimeInterval;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@Table(name = "batch")
@NoArgsConstructor
public class Batch extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "time_interval_id")
    private TimeInterval timeInterval;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "batch_number", unique = true)
    private String batchNumber;

    @ManyToOne
    @JoinColumn(name = "batch_config_id")
    private BatchConfig batchConfig;

    @ManyToOne
    @JoinColumn(name = "cook_id")
    private User cook;

    @Column(name = "status", nullable = false)
    private BatchStatus status;

    @OneToMany(mappedBy = "batch")
    private Set<BatchOrderItem> batchOrderItems = new HashSet<>();

//        public OrderItem(OrderItem other) {
//                this.id = null; // New instance should not have the same ID
//                this.order = other.order != null ? new Order(other.order) : null; // Deep copy of Order
//                this.menuItem = other.menuItem != null ? new MenuItem(other.menuItem) : null; // Deep copy of MenuItem
//                this.combo = other.combo != null ? new Combo(other.combo) : null; // Deep copy of Combo
//                this.quantity = other.quantity;
//                this.price = other.price;
//                this.createdDttm = other.createdDttm;
//                this.updatedDttm = other.updatedDttm;
//        }
//
//        public Order getOrder() {
//                return order != null ? new Order(order) : null; // Defensive copy
//        }
//
//        public void setOrder(Order order) {
//                this.order = order != null ? new Order(order) : null; // Defensive copy
//        }
//
//        public MenuItem getMenuItem() {
//                return menuItem != null ? new MenuItem(menuItem) : null; // Defensive copy
//        }
//
//        public void setMenuItem(MenuItem menuItem) {
//                this.menuItem = menuItem != null ? new MenuItem(menuItem) : null; // Defensive copy
//        }
//
//        public Combo getCombo() {
//                return combo != null ? new Combo(combo) : null; // Defensive copy
//        }
//
//        public void setCombo(Combo combo) {
//                this.combo = combo != null ? new Combo(combo) : null; // Defensive copy
//        }
}