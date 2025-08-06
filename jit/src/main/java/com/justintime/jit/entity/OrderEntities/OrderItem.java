package com.justintime.jit.entity.OrderEntities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.MenuItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Audited
@Table(name = "order_item")
@NoArgsConstructor
public class OrderItem extends BaseEntity {

        @ManyToOne
        @JoinColumn(name = "order_id", nullable = false)
        private Order order;

        @ManyToOne
        @JoinColumn(name = "menu_item_id", nullable = false)
        private MenuItem menuItem;

        @ManyToOne
        @JoinColumn(name = "combo_id", nullable = false)
        private Combo combo;

        @Column(name = "quantity", nullable = false, columnDefinition = "int default 1")
        private int quantity;

        @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10,2)")
        private BigDecimal price;
}