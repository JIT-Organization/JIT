package com.justintime.jit.repository;

import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.MenuItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends BaseRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);
    List<MenuItem> findByMenuItemNameContaining(String restaurantName);
//MenuItem findByMenuItemNameAndCategoryId(String menuItemName, Long categoryId);
}
