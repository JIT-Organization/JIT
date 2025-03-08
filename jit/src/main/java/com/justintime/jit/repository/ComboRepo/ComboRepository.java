package com.justintime.jit.repository.ComboRepo;

import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboRepository extends BaseRepository<Combo, Long> {

//    @Query("SELECT new com.justintime.jit.dto.ComboDTO(" +
//            "c.id, c.comboName, c.description, c.price, c.offerPrice, c.offerFrom, c.offerTo, " +
//            "c.stock, c.count, c.preparationTime, c.acceptBulkOrders, c.onlyVeg, c.active, " +
//            "c.hotelSpecial, c.base64Image, c.rating, c.createdDttm, c.updatedDttm, " +
//            "(SELECT new com.justintime.jit.dto.ComboItemDTO(ci.id, ci.menuItem.id, ci.comboItemName, ci.quantity) " +
//            " FROM c.comboItemSet ci), " +
//            "(SELECT cat.categoryName FROM c.categories cat), " +
//            "(SELECT new com.justintime.jit.dto.TimeIntervalDTO(ti.startTime, ti.endTime) " +
//            " FROM c.timeIntervalSet ti)) " +
//            "FROM Combo c")
//    List<ComboDTO> findAllDTO();


    List<Combo> findByRestaurantId(Long restaurantId);
//    Combo findByComboNameAndCategoryId(String comboName, Long categoryId);
}

