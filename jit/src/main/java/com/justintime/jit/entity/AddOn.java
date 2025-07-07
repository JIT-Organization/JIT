package com.justintime.jit.entity;

import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.Enums.BatchStatus;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.util.mapper.AddOnOption;
import com.justintime.jit.util.mapper.AddOnOptionsConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@Table(name = "add_on")
@NoArgsConstructor
public class AddOn extends BaseEntity {

    @Column(name = "label")
    private String label;

    @Convert(converter = AddOnOptionsConverter.class)
    @Column(name = "options", columnDefinition = "JSON")
    private List<AddOnOption> options;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_add_on",
            joinColumns = @JoinColumn(name = "add_on_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"add_on_id", "menu_item_id"})
    )
    private Set<MenuItem> menuItemSet = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "order_item_add_on",
            joinColumns = @JoinColumn(name = "add_on_id"),
            inverseJoinColumns = @JoinColumn(name = "order_item_id")
    )
    private Set<OrderItem> orderItemSet = new HashSet<>();
}