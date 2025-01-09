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
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="category_name", nullable = false)
    private String categoryName;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("category")
    private List<Food> foods;

    // Copy Constructor
    public Category(Category other) {
        this.id = other.id;
        this.categoryName = other.categoryName;
        this.createdDttm = other.createdDttm;
        this.updatedDttm = other.updatedDttm;
        this.foods = other.foods != null ? other.foods.stream().map(Food::new).collect(Collectors.toList()) : null; // Deep copy of foods
    }

    public List<Food> getFoods() {
        return foods != null ? foods.stream().map(Food::new).collect(Collectors.toList()) : null; // Defensive copy
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods != null ? foods.stream().map(Food::new).collect(Collectors.toList()) : null; // Defensive copy
    }
}