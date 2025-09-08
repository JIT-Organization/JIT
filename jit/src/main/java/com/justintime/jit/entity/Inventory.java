package com.justintime.jit.entity;

import com.justintime.jit.entity.OrderEntities.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.List;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class Inventory {

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "expense", nullable = false)
    private float expense;

    @Column(name = "item", nullable = false)
    private String item;

    @Column(name = "unit", nullable = false)
    private String unit;

    // One-to-One mapping with Supplier (User entity acting as Supplier)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private User supplier;

    // One-to-Many mapping with OrderItem
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}