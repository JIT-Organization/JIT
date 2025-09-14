package com.justintime.jit.entity;

import com.justintime.jit.entity.OrderEntities.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "expense", nullable = false, precision = 12, scale = 2)
    private BigDecimal expense;

    @Column(name = "item", nullable = false)
    private String item;

    @Column(name = "unit", nullable = false)
    private String unit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private User supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
