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

    @UpdateTimestamp
    @Column(name = "offer_from")
    private LocalDateTime offerFrom;

    @UpdateTimestamp
    @Column(name = "offer_to")
    private LocalDateTime offerTo;

    @Column(name = "stock", columnDefinition = "INT DEFAULT 0")
    private Integer stock = 0;

    @Column(name = "count", columnDefinition = "INT DEFAULT 0")
    private Integer count = 0;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_cook", // Join table name
            joinColumns = @JoinColumn(name = "menu_item_id"), // Foreign key for MenuItem
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

    @Override
    public String getName() {
        return this.menuItemName;
    }

    @Override
    public Boolean isCombo() {
        return false;
    }

}