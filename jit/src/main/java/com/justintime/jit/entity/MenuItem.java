package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.util.filter.FilterableItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @JsonManagedReference
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

    @Column(name = "food_type")
    private FoodType foodType; // MenuItem, Variant

    @Column(name = "offer_price", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal offerPrice;

    @Column(name = "offer_from")
    private LocalDateTime offerFrom;

    @Column(name = "offer_to")
    private LocalDateTime offerTo;

    @Column(name = "availability")
    private String availability;

    @Column(name = "stock", columnDefinition = "INT DEFAULT 0")
    private Integer stock = 0;

    @Column(name = "count", columnDefinition = "INT DEFAULT 0")
    private Integer count = 0;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_cook",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> cookSet = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "menu_item_time_interval",
            joinColumns = @JoinColumn(name = "menu_item_id"),
            inverseJoinColumns = @JoinColumn(name = "time_interval_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"menu_item_id", "time_interval_id"})
    )
    private Set<TimeInterval> timeIntervalSet = new HashSet<>();

    @ManyToMany(mappedBy = "menuItemSet", cascade = {CascadeType.MERGE})
    private Set<AddOn> addOnSet = new HashSet<>();

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

    public Set<DayOfWeek> getAvailability() {
        if (this.availability == null || this.availability.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(this.availability.split(","))
                .map(String::trim)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }

    public void setAvailability(Set<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            this.availability = null;
        } else {
            this.availability = days.stream()
                    .map(DayOfWeek::name)
                    .collect(Collectors.joining(","));
        }
    }


}