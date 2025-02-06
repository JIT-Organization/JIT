package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.justintime.jit.entity.ComboEntities.Combo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "time_interval")
@Getter
@Setter
@NoArgsConstructor
@Audited
public class TimeInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "timeIntervalSet")
    @JsonIgnoreProperties("timeIntervalSet")
    private Set<MenuItem> menuItemSet = new HashSet<>();

    @ManyToMany(mappedBy = "timeIntervalSet", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnoreProperties("timeIntervalSet")
    private Set<Combo> comboSet = new HashSet<>();

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
}