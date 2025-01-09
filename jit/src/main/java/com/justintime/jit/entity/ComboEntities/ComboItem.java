package com.justintime.jit.entity.ComboEntities;

import com.justintime.jit.entity.MenuItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "combo_item")
public class ComboItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "combo_item_combo",
            joinColumns = @JoinColumn(name = "combo_item_id"),
            inverseJoinColumns = @JoinColumn(name = "combo_id")
    )
    private Set<Combo> comboSet = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @CreationTimestamp
    @Column(name = "created_dttm", nullable = false, updatable = false)
    private LocalDateTime createdDttm;

    @UpdateTimestamp
    @Column(name = "updated_dttm", nullable = false)
    private LocalDateTime updatedDttm;

    public Set<Combo> getComboSet() {
        return Collections.unmodifiableSet(comboSet);
    }

    public void setComboSet(Set<Combo> comboSet) {
        this.comboSet = comboSet != null ? new HashSet<>(comboSet) : new HashSet<>();
    }

    public MenuItem getMenuItem() {
        return menuItem == null ? null : new MenuItem(menuItem);
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem == null ? null : new MenuItem(menuItem);
    }

    public ComboItem(ComboItem other) {
        this.id = null; // Ensure new instance doesn't have the same ID
        this.comboSet = new HashSet<>(other.comboSet); // Deep copy of comboSet if necessary
        this.menuItem = new MenuItem(other.menuItem); // Deep copy of MenuItem, assuming MenuItem has a copy constructor
        this.createdDttm = other.createdDttm;
        this.updatedDttm = other.updatedDttm;
    }


}