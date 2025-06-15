package com.justintime.jit.entity;

import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "time_interval")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class TimeInterval extends BaseEntity{

    @ManyToMany(mappedBy = "timeIntervalSet")
    private Set<MenuItem> menuItemSet = new HashSet<>();

    @ManyToMany(mappedBy = "timeIntervalSet", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Combo> comboSet = new HashSet<>();

    @OneToMany(mappedBy = "timeInterval", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}