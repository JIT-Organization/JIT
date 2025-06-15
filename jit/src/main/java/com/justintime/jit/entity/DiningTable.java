package com.justintime.jit.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dining_table")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class DiningTable extends BaseEntity{

    @Column(name = "tableNumber", nullable = false)
    private String tableNumber;

    @Column(name = "chairs", nullable = false)
    private Integer chairs;

//    @Type(type = "yes_no")
    @Column(name="availability")
    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToMany(mappedBy = "diningTableSet")
    private Set<Reservation> reservationSet = new HashSet<>();

}
