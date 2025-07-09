package com.justintime.jit.repository;

import com.justintime.jit.entity.AddOn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddOnRepository extends BaseRepository<AddOn, Long> {

    Optional<AddOn> findByLabel(String label);
    List<AddOn> findAllByRestaurant_RestaurantCode(String restaurantCode);

    @Query(value = "SELECT a.* FROM add_on a " +
            "JOIN menu_item_add_on mia ON a.id = mia.add_on_id " +
            "JOIN menu_item mi ON mia.menu_item_id = mi.id " +
            "JOIN restaurant r ON mi.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND mi.menu_item_name = :menuItemName", nativeQuery = true)
    List<AddOn> findAllByRestaurantCodeAndMenuItemName(
            @Param("restaurantCode") String restaurantCode,
            @Param("menuItemName") String menuItemName
    );

    @Query(value = "SELECT a.* FROM add_on a " +
            "JOIN combo_add_on ca ON a.id = ca.add_on_id " +
            "JOIN combo c ON ca.combo_id = c.id " +
            "JOIN restaurant r ON c.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND c.combo_name = :comboName", nativeQuery = true)
    List<AddOn> findAllByRestaurantCodeAndComboName(
            @Param("restaurantCode") String restaurantCode,
            @Param("comboName") String comboName
    );

    @Query(value = "SELECT a.* FROM add_on a " +
            "JOIN order_item oi ON a.orderItemID = oi.id " +
            "JOIN \"order\" o ON oi.order_id = o.id " +
            "JOIN restaurant r ON oi.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode " +
            "AND o.order_number = :orderNumber " +
            "AND oi.order_item_name = :orderItemName", nativeQuery = true)
    List<AddOn> findAddOnsByRestaurantCodeAndOrderNumberAndOrderItemName(
            @Param("restaurantCode") String restaurantCode,
            @Param("orderNumber") String orderNumber,
            @Param("orderItemName") String orderItemName
    );

}
