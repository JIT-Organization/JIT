package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.OrderEntities.Order;
import jakarta.persistence.*;
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
@Table(name = "restaurant")
@NoArgsConstructor
public class Restaurant extends BaseEntity{

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
        private List<Admin> admins;

        @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
        private List<Category> categories;

//        // Copy Constructor
//        public Restaurant(Restaurant other) {
//                this.id = other.id;
//                this.restaurantName = other.restaurantName;
//                this.contactNumber = other.contactNumber;
//                this.email = other.email;
//                this.createdDttm = other.createdDttm;
//                this.updatedDttm = other.updatedDttm;
//                this.addresses = other.addresses != null ? other.addresses.stream().map(Address::new).collect(Collectors.toList()) : null;
//                this.menu = other.menu != null ? other.menu.stream().map(MenuItem::new).collect(Collectors.toList()) : null;
//                this.orders = other.orders != null ? other.orders.stream().map(Order::new).collect(Collectors.toList()) : null;
//                this.shiftCapacities = other.shiftCapacities != null ? other.shiftCapacities.stream().map(ShiftCapacity::new).collect(Collectors.toList()) : null;
//                this.reservations = other.reservations != null ? other.reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null;
//                this.admins = other.admins != null ? other.admins.stream().map(Admin::new).collect(Collectors.toList()) : null;
//        }
//
//        public List<Address> getAddresses() {
//                return addresses != null ? addresses.stream().map(Address::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setAddresses(List<Address> addresses) {
//                this.addresses = addresses != null ? addresses.stream().map(Address::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public List<MenuItem> getMenu() {
//                return menu != null ? menu.stream().map(MenuItem::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setMenu(List<MenuItem> menu) {
//                this.menu = menu != null ? menu.stream().map(MenuItem::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public List<Order> getOrders() {
//                return orders != null ? orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setOrders(List<Order> orders) {
//                this.orders = orders != null ? orders.stream().map(Order::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public List<ShiftCapacity> getShiftCapacities() {
//                return shiftCapacities != null ? shiftCapacities.stream().map(ShiftCapacity::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setShiftCapacities(List<ShiftCapacity> shiftCapacities) {
//                this.shiftCapacities = shiftCapacities != null ? shiftCapacities.stream().map(ShiftCapacity::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public List<Reservation> getReservations() {
//                return reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setReservations(List<Reservation> reservations) {
//                this.reservations = reservations != null ? reservations.stream().map(Reservation::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public List<Admin> getAdmins() {
//                return admins != null ? admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
//
//        public void setAdmins(List<Admin> admins) {
//                this.admins = admins != null ? admins.stream().map(Admin::new).collect(Collectors.toList()) : null; // Defensive copy
//        }
}