package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.util.filter.FilterableItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Audited
@Table(name = "menu_item")
@Getter
@Setter
@NoArgsConstructor
public class MenuItem extends BaseEntity implements FilterableItem {

    @Column(name="menu_item_name", length = 100)
    private String menuItemName;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "category_menu_item",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categorySet = new HashSet<>();

    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(name = "offer_price", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal offerPrice;

    @Column(name = "offer_from")
    private LocalDateTime offerFrom;

    @Column(name = "offer_to")
    private LocalDateTime offerTo;

    @Column(name = "stock", columnDefinition = "INT DEFAULT 0")
    private Integer stock = 0;

    @Column(name = "count", columnDefinition = "INT DEFAULT 0")
    private Integer count = 0;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_cook",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "cook_id")
    )
    private Set<Cook> cookSet = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_time_interval",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "time_interval_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_id", "time_interval_id"})
    )
    private Set<TimeInterval> timeIntervalSet = new HashSet<>();

    @Column(name = "preparation_time")
    private Integer preparationTime;

    @Column(name = "is_preparation_time_for_single_menu_item", length=1)
    private Boolean isPreparationTimeForSingleMenuItem = true;

    @Column(name = "max_clubbed_menu_items")
    private Integer maxClubbedMenuItems = 1;

    @Column(name = "accept_bulk_orders", length = 1)
    private Boolean acceptBulkOrders;

    @Column(name = "only_veg", length = 1)
    private Boolean onlyVeg;

    @Column(name = "only_for_combos", length = 1)
    private Boolean onlyForCombos;

    @Column(name = "active", length = 1)
    private Boolean active;

    @Column(name = "hotel_special", length = 1)
    private Boolean hotelSpecial;

    @Column(name = "image", columnDefinition = "TEXT")
    private String base64Image;

    @Column(name = "rating", columnDefinition = "DECIMAL(10,1)")
    private BigDecimal rating;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL)
    private List<ComboItem> comboItems = new ArrayList<>();

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "batch_config_id")
    private BatchConfig batchConfig;

    @Override
    public String getName() {
        return this.menuItemName;
    }

    @Override
    public Boolean isCombo() {
        return false;
    }

//    // Copy Constructor
//    public MenuItem(MenuItem other) {
//        this.id = null; // New instance should not copy the ID (leave it null for persistence)
//        this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
//        this.food = other.food != null ? new Food(other.food) : null;
//        this.price = other.price;
//        this.stock = other.stock;
//        this.createdDttm = other.createdDttm;
//        this.updatedDttm = other.updatedDttm;
//
//        // Deep copy the comboItems
//        this.comboItems = other.comboItems.stream()
//                .map(ComboItem::new) // Assuming ComboItem also has a copy constructor
//                .toList();
//
//        // Deep copy the orderItems
//        this.orderItems = other.orderItems.stream()
//                .map(OrderItem::new) // Assuming OrderItem also has a copy constructor
//                .toList();
//    }
//
//    public Restaurant getRestaurant() {
//        return restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
//    }
//
//    public void setRestaurant(Restaurant restaurant) {
//        this.restaurant = restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
//    }
//
//    public Food getFood() {
//        return food != null ? new Food(food) : null; // Defensive copy
//    }
//
//    public void setFood(Food food) {
//        this.food = food != null ? new Food(food) : null; // Defensive copy
//    }
//
//    public List<ComboItem> getComboItems() {
//        return Collections.unmodifiableList(comboItems);
//    }
//
//    public void setComboItems(List<ComboItem> comboItems) {
//        this.comboItems = comboItems != null ? new ArrayList<>(comboItems) : new ArrayList<>();
//    }
//
//    public List<OrderItem> getOrderItems() {
//        return Collections.unmodifiableList(orderItems);
//    }
//
//    public void setOrderItems(List<OrderItem> orderItems) {
//        this.orderItems = orderItems != null ? new ArrayList<>(orderItems) : new ArrayList<>();
//    }
}