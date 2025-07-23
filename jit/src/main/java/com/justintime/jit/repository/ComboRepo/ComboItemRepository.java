package com.justintime.jit.repository.ComboRepo;

import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ComboItemRepository extends BaseRepository<ComboItem,Long> {
    @Query(value = "SELECT ci.* FROM combo_item ci " +
            "JOIN combo_item_combo cic ON ci.id = cic.combo_item_id " +
            "JOIN combo c ON cic.combo_id = c.id " +
            "JOIN menu_item mi ON ci.menu_item_id = mi.id " +
            "WHERE c.restaurant_id = :restaurantId " +
            "AND mi.menu_item_name IN (:comboItemNames)", nativeQuery = true)
    Set<ComboItem> findByRestaurantIdAndComboItemNames(Long restaurantId, Set<String> comboItemNames);
}
