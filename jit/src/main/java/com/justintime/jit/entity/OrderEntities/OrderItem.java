package com.justintime.jit.entity.OrderEntities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.entity.Enums.OrderItemStatus;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.util.CodeNumberGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@Audited
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {

        @ManyToOne
        @JoinColumn(name = "order_id", nullable = false)
        private Order order;

        @ManyToOne
        @JoinColumn(name = "menu_item_id")
        private MenuItem menuItem;

        @ManyToOne
        @JoinColumn(name = "combo_id")
        private Combo combo;

        @Column(name = "quantity", columnDefinition = "int default 1")
        private int quantity;

        @Column(name = "price", columnDefinition = "DECIMAL(10,2)")
        private BigDecimal price;

        @Column(name = "total_price", columnDefinition = "DECIMAL(10,2)")
        private BigDecimal totalPrice;

        @Column(name = "food_type")
        private FoodType foodType;

        @Enumerated(EnumType.STRING)
        private OrderItemStatus orderItemStatus;

        @Column(name = "custom_notes", length = 500)
        private String customNotes;

        @ManyToOne
        @JoinColumn(name = "time_interval_id")
        private TimeInterval timeInterval;

        @ManyToMany(mappedBy = "orderItemSet", cascade = {CascadeType.MERGE})
        private Set<AddOn> addOnSet = new HashSet<>();

        @OneToMany(mappedBy = "orderItem")
        private Set<BatchOrderItem> batchOrderItems = new HashSet<>();

        @Column(name = "max_time_limit_to_start")
        private LocalDateTime maxTimeLimitToStart;

        @ManyToOne
        private OrderItem parentItem;

        @OneToMany(mappedBy = "parentItem", cascade = CascadeType.ALL)
        private List<OrderItem> subItems = new ArrayList<>();

        @ManyToOne
        @JoinColumn(name = "assigned_cook_id")
        private User cook;

        @PrePersist
        @PreUpdate
        public void calculateMaxTimeLimitToStart() {
            if (Objects.nonNull(order) && Objects.nonNull(order.getOrderDate())&& Objects.nonNull(menuItem)  && Objects.nonNull( menuItem.getBatchConfig())) {
                Integer prepTime = menuItem.getBatchConfig().getPreparationTime();
                if (Objects.nonNull(prepTime)) {
                    this.maxTimeLimitToStart = order.getOrderDate().minusMinutes(prepTime);
                }
            }
        }
}