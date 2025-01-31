package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.OrderEntities.OrderItem;
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
@Table(
        name = "menu_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "address_id", "food_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    @JsonIgnoreProperties("menuItems")
    private Address address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnoreProperties("menu")
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id", nullable = false)
    @JsonIgnoreProperties("menuItems")
    private Food food;

    @Column(name="description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(name = "offer_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal offerPrice;

    @Column(name = "stock", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stock = 0;

    @Column(name = "count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer count = 0;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_cook", // Join table name
            joinColumns = @JoinColumn(name = "menu_item_id"), // Foreign key for MenuItem
            inverseJoinColumns = @JoinColumn(name = "cook_id"), // Foreign key for Cook
            uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_id", "cook_id"})
    )
    @JsonIgnoreProperties("menuItemSet")
    private Set<Cook> cookSet = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_time_interval",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "time_interval_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_id", "time_interval_id"})
    )
    @JsonIgnoreProperties("menuItemSet")
    private Set<TimeInterval> timeIntervalSet = new HashSet<>();

    @Column(name = "preparation_time", nullable = false)
    private Integer preparationTime;

    @Column(name = "food_type", nullable = false, length = 1)
    private Boolean onlyVeg;

    @Column(name = "only_for_combos", nullable = false, length = 1)
    private Boolean onlyForCombos;

    @Column(name = "hotel_special", nullable = false, length = 1)
    private Boolean hotelSpecial;

    @Column(name = "image", columnDefinition = "TEXT")
    private String base64Image;

    @Column(name = "rating", nullable = false, columnDefinition = "DECIMAL(10,1)")
    private BigDecimal rating;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("menuItem")
    private List<ComboItem> comboItems = new ArrayList<>();

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("menuItem")
    private List<OrderItem> orderItems = new ArrayList<>();

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