package com.justintime.jit.entity.ComboEntities;

import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.util.filter.FilterableItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@Table(name = "combo")
@NoArgsConstructor
public class Combo extends BaseEntity implements FilterableItem {

    @Column(name = "combo_name", unique = true, nullable = false)
    private String comboName;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "combo_item_combo",
            joinColumns = @JoinColumn(name = "combo_id"),
            inverseJoinColumns = @JoinColumn(name = "combo_item_id")
    )
    private Set<ComboItem> comboItemSet = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "combo_category",
            joinColumns = @JoinColumn(name = "combo_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categorySet = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(name = "stock", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stock = 0;

    @Column(name="description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "offer_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal offerPrice;

    @UpdateTimestamp
    @Column(name = "offer_from")
    private LocalDateTime offerFrom;

    @UpdateTimestamp
    @Column(name = "offer_to")
    private LocalDateTime offerTo;

    @Column(name = "count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer count = 0;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "combo_time_interval",
            joinColumns = @JoinColumn(name = "combo_id"),
            inverseJoinColumns = @JoinColumn(name = "time_interval_id")
    )
    private Set<TimeInterval> timeIntervalSet = new HashSet<>();

    @ManyToMany(mappedBy = "comboSet", cascade = {CascadeType.MERGE})
    private Set<AddOn> addOnSet = new HashSet<>();

    @Column(name = "preparation_time", nullable = false)
    private Integer preparationTime;

    @Column(name = "accept_bulk_orders", nullable = false, length = 1)
    private Boolean acceptBulkOrders;

    @Column(name = "only_veg", nullable = false, length = 1)
    private Boolean onlyVeg;

    @Column(name = "active", nullable = false, length = 1)
    private Boolean active;

    @Column(name = "hotel_special", nullable = false, length = 1)
    private Boolean hotelSpecial;

    @Column(name = "image", columnDefinition = "TEXT")
    private String base64Image;

    @Column(name = "rating", nullable = false, columnDefinition = "DECIMAL(10,1)")
    private BigDecimal rating;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Override
    public String getName() {
        return this.comboName;
    }

    @Override
    public Boolean getOnlyForCombos() {
        return false;
    }

    @Override
    public FoodType getFoodType() {
        return FoodType.COMBO;
    }

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