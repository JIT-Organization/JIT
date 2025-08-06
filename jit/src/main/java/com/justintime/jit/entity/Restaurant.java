package com.justintime.jit.entity;

import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.OrderEntities.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@Table(name = "restaurant")
@NoArgsConstructor
public class Restaurant extends BaseEntity{

        @Column(name="restaurant_code", length = 12, unique = true, nullable = false)
        private String restaurantCode;

        @Column(name = "restaurant_name", nullable = false)
        private String restaurantName;

        @Column(name = "contact_number")
        private String contactNumber;

        @Column(name = "email")
        private String email;

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

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<MenuItem> menu;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<Combo> combos;

        @OneToMany(mappedBy = "restaurant")
        private List<Cook> cooks;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<Order> orders;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<ShiftCapacity> shiftCapacities;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<Reservation> reservations;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<DiningTable> diningTables;

        @ManyToMany(cascade = CascadeType.REMOVE)
        @JoinTable(
                name = "user_restaurant",
                joinColumns = @JoinColumn(name = "restaurant_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id")
        )
        private Set<User> users;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<Category> categories;
}