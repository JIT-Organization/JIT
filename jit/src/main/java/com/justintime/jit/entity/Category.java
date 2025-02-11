package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.justintime.jit.entity.ComboEntities.Combo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="category_name", unique = true, nullable = false)
    private String categoryName;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "category_menu_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private Set<MenuItem> menuItems = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "combo_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "combo_id")
    )
    private Set<Combo> combos = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;


//    // Copy Constructor
//    public Category(Category other) {
//        this.id = other.id;
//        this.categoryName = other.categoryName;
//        this.createdDttm = other.createdDttm;
//        this.updatedDttm = other.updatedDttm;
//        this.foods = other.foods != null ? other.foods.stream().map(Food::new).collect(Collectors.toList()) : null;
//    }
//
//    // Defensive Copying: Get a copy of the list of foods to prevent external modifications
//    public List<Food> getFoods() {
//        return foods != null ? foods.stream().map(Food::new).collect(Collectors.toList()) : null;
//    }
//
//    // Defensive Copying: Set a copy of the list of foods
//    public void setFoods(List<Food> foods) {
//        this.foods = foods != null ? foods.stream().map(Food::new).collect(Collectors.toList()) : null;
//    }
}