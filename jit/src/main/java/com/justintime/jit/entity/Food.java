package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="food_name", nullable = false, length = 100)
    private String foodName;

    @Column(name="description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties("foods")
    private Category category;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("food")
    private List<MenuItem> menuItems;

    // Copy Constructor
    public Food(Food other) {
        this.id = other.id;
        this.foodName = other.foodName;
        this.description = other.description;
        this.category = other.category != null ? new Category(other.category) : null;
        this.createdDttm = other.createdDttm;
        this.updatedDttm = other.updatedDttm;
        this.menuItems = other.menuItems != null ? other.menuItems.stream().map(MenuItem::new).collect(Collectors.toList()) : null; // Deep copy of menuItems
    }

    public Category getCategory() {
        return category != null ? new Category(category) : null; // Defensive copy
    }

    public void setCategory(Category category) {
        this.category = category != null ? new Category(category) : null; // Defensive copy
    }

    public List<MenuItem> getMenuItems() {
        return menuItems != null ? menuItems.stream().map(MenuItem::new).collect(Collectors.toList()) : null; // Defensive copy
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems != null ? menuItems.stream().map(MenuItem::new).collect(Collectors.toList()) : null; // Defensive copy
    }
}