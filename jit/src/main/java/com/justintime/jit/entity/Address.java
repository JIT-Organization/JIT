package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Getter
@Setter
@Table(name = "address")
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnoreProperties("addresses")
    private Restaurant restaurant;

    @Column(name = "address_line1", nullable = false, length = 150)
    private String addressLine1;

    @Column(name = "address_line2", length = 150)
    private String addressLine2;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    // Copy Constructor
    public Address(Address other) {
        this.id = other.id;
        this.restaurant = other.restaurant != null ? new Restaurant(other.restaurant) : null;
        this.addressLine1 = other.addressLine1;
        this.addressLine2 = other.addressLine2;
        this.city = other.city;
        this.state = other.state;
        this.country = other.country;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.createdDttm = other.createdDttm;
        this.updatedDttm = other.updatedDttm;
    }

    public Restaurant getRestaurant() {
        return restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant != null ? new Restaurant(restaurant) : null; // Defensive copy
    }
}