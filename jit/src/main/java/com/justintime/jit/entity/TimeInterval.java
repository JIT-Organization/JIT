package com.justintime.jit.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
public class TimeInterval extends BaseEntity{

    @ManyToMany(mappedBy = "timeIntervalSet")
    private Set<MenuItem> menuItemSet = new HashSet<>();

    @ManyToMany(mappedBy = "timeIntervalSet", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Combo> comboSet = new HashSet<>();

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
}