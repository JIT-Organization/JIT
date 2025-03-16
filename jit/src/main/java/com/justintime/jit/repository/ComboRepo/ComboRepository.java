package com.justintime.jit.repository.ComboRepo;

import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ComboRepository extends BaseRepository<Combo, Long> {

    List<Combo> findByRestaurantId(Long restaurantId);
    @Query(value = "SELECT c.* FROM combo c " +
            "WHERE c.combo_name IN (:formattedComboNames) " +
            "AND c.restaurant_id = :restaurantId",
            nativeQuery = true)
    Set<Combo> findByComboNamesAndRestaurantId(@Param("formattedComboNames") Set<String> formattedComboNames,
                                               @Param("restaurantId") Long restaurantId);
}

