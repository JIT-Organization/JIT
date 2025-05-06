package com.justintime.jit.entity;

import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.util.CodeNumberGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "kitchen_set")
public class KitchenSet extends BaseEntity {
    @Column(name = "kitchen_set_number", unique = true, nullable = false, updatable = false)
    private String kitchenSetNumber;

    @PrePersist
    protected void onCreate() {
        this.kitchenSetNumber = CodeNumberGenerator.generateCode("kitchenSet");
    }

    private String kitchenSetName;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private String maxCount;

    @OneToMany(mappedBy = "kitchenSet", fetch = FetchType.LAZY)
    private List<MenuItem> menuItems;

    private Integer estdBatchPrepTime;
}
