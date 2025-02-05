package com.justintime.jit.entity.ComboEntities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.justintime.jit.entity.Address;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@Table(name = "combo")
@NoArgsConstructor
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "combo_item_combo",
            joinColumns = @JoinColumn(name = "combo_id"),
            inverseJoinColumns = @JoinColumn(name = "combo_item_id")
    )
    private Set<ComboItem> comboItemSet = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnoreProperties("combos")
    private Restaurant restaurant;

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(name = "stock", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stock = 0;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("combo")
    private List<OrderItem> orderItems;

//    public Set<ComboItem> getComboItemSet() {
//        return Collections.unmodifiableSet(comboItemSet);
//    }
//
//    public void setComboItemSet(Set<ComboItem> comboItemSet) {
//        this.comboItemSet = comboItemSet != null
//                ? new HashSet<>(comboItemSet) // Defensive copy
//                : new HashSet<>();
//    }
//
//    public List<OrderItem> getOrderItems() {
//        return Collections.unmodifiableList(orderItems);
//    }
//
//    public void setOrderItems(List<OrderItem> orderItems) {
//        this.orderItems = orderItems != null
//                ? orderItems.stream().map(OrderItem::new).collect(Collectors.toList()) // Defensive copy
//                : null;
//    }
//
//    public Combo(Combo other) {
//        this.id = other.id;
//        this.price = other.price;
//        this.stock = other.stock;
//        this.createdDttm = other.createdDttm;
//        this.updatedDttm = other.updatedDttm;
//
//        // Deep copy of comboItemSet to avoid sharing mutable objects
//        this.comboItemSet = other.comboItemSet != null
//                ? other.comboItemSet.stream().map(ComboItem::new).collect(Collectors.toSet())
//                : new HashSet<>();
//
//        // Deep copy of orderItems to avoid sharing mutable objects
//        this.orderItems = other.orderItems != null
//                ? other.orderItems.stream().map(OrderItem::new).collect(Collectors.toList())
//                : null;
//    }
//
//    // Updated constructor with defensive copying
//    public Combo(Long id, Set<ComboItem> comboItemSet, Double price, Integer stock, LocalDateTime createdDttm, LocalDateTime updatedDttm, List<OrderItem> orderItems) {
//        this.id = id;
//        this.price = price;
//        this.stock = stock;
//        this.createdDttm = createdDttm;
//        this.updatedDttm = updatedDttm;
//
//        // Defensive copy of comboItemSet
//        this.comboItemSet = comboItemSet != null
//                ? comboItemSet.stream().map(ComboItem::new).collect(Collectors.toSet())
//                : new HashSet<>();
//
//        // Defensive copy of orderItems
//        this.orderItems = orderItems != null
//                ? orderItems.stream().map(OrderItem::new).collect(Collectors.toList())
//                : null;
//    }
}