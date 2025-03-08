package com.justintime.jit.repository;

import com.justintime.jit.dto.CategoryDTO;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.MenuItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends BaseRepository<Category,Long> {
    boolean existsByCategoryName(String categoryName);

    Category findByCategoryName(String categoryName);

//    @Query(value = "SELECT DISTINCT c.* FROM category c " +
//            "JOIN category_menu_item mc ON c.id = mc.category_id " +
//            "JOIN menu_item m ON mc.menu_item_id = m.id " +
//            "WHERE m.restaurant_id = :restaurantId",
//            nativeQuery = true)
//    List<Category> findDistinctCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);

    List<Category> findByRestaurantId(Long restaurantId);


}
