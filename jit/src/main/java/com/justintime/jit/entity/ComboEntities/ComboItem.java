package com.justintime.jit.entity.ComboEntities;

import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.MenuItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(name = "combo_item")
public class ComboItem extends BaseEntity {

    @Column(name = "quantity", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer quantity = 1;

    @ManyToMany(mappedBy = "comboItemSet")
    private Set<Combo> comboSet = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

//
//    public Set<Combo> getComboSet() {
//        return Collections.unmodifiableSet(comboSet);
//    }
//
//    public void setComboSet(Set<Combo> comboSet) {
//        this.comboSet = comboSet != null ? new HashSet<>(comboSet) : new HashSet<>();
//    }
//
//    public MenuItem getMenuItem() {
//        return menuItem == null ? null : new MenuItem(menuItem);
//    }
//
//    public void setMenuItem(MenuItem menuItem) {
//        this.menuItem = menuItem == null ? null : new MenuItem(menuItem);
//    }
//
//    public ComboItem(ComboItem other) {
//        this.id = null; // Ensure new instance doesn't have the same ID
//        this.comboSet = new HashSet<>(other.comboSet); // Deep copy of comboSet if necessary
//        this.menuItem = new MenuItem(other.menuItem); // Deep copy of MenuItem, assuming MenuItem has a copy constructor
//        this.createdDttm = other.createdDttm;
//        this.updatedDttm = other.updatedDttm;
//    }


}