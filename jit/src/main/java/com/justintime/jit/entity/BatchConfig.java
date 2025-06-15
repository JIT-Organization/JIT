package com.justintime.jit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited
@Table(name = "batch_config")
public class BatchConfig extends BaseEntity {

    @Column(name = "batch_config_name")
    private String batchConfigName;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "max_count")
    private String maxCount;

    @ManyToMany
    @JoinTable(
        name = "batch_config_cook",
        joinColumns = @JoinColumn(name = "batch_config_id"),
        inverseJoinColumns = @JoinColumn(name = "cook_id")
    )
    private Set<User> cooks = new HashSet<>();

    @OneToMany(mappedBy = "batchConfig")
    private Set<MenuItem> menuItems = new HashSet<>();

    @OneToMany(mappedBy = "batchConfig", fetch = FetchType.LAZY)
    private List<Batch> batches;

    @Column(name = "preparation_time")
    private Integer preparationTime;

    @Column(name = "batch_config_number")
    private String batchConfigNumber;
}
