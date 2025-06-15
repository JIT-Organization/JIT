package com.justintime.jit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.DayOfWeek;

@Entity
@Audited
@Table(name = "menu_item_available_day")
@Getter
@Setter
@NoArgsConstructor
public class MenuItemAvailableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day")
    private DayOfWeek day;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;
}
