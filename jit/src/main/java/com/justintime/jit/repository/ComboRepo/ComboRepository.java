package com.justintime.jit.repository.ComboRepo;

import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboRepository extends BaseRepository<Combo, Long> {

    List<Combo> findByRestaurantId(Long restaurantId);
    @Query(value = "SELECT c.* FROM combo c JOIN combo_category cc ON c.id = cc.combo_id WHERE cc.category_id = :categoryId AND c.combo_name = :comboName", nativeQuery = true)
    Combo findByComboNameAndCategoryId(@Param("comboName") String comboName, @Param("categoryId") Long categoryId);
}

