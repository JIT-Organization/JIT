package com.justintime.jit.repository;

import com.justintime.jit.entity.MenuItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends BaseRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long retaurantId);
    List<MenuItem> findByMenuItemNameContaining(String restaurantName);
}
