package com.justintime.jit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name= "max_count")
    private String maxCount;

    @OneToMany(mappedBy = "batchConfig", fetch = FetchType.LAZY)
    private List<MenuItem> menuItems;

    @OneToMany(mappedBy = "batchConfig", fetch = FetchType.LAZY)
    private List<Batch> batches;

    @Column(name = "estd_batch_prep_time")
    private Integer estdBatchPrepTime;
}
