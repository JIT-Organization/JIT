package com.justintime.jit.repository;

import com.justintime.jit.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends BaseRepository<Category,Long> {
    boolean existsByCategoryName(String categoryName);

    Category findByCategoryName(String categoryName);

    List<Category> findByRestaurantId(Long restaurantId);

    Optional<Category> findByRestaurantIdAndId(Long restaurantId, Long Id);

    List<Category> findByRestaurant_RestaurantCode(String restaurantCode);

    Boolean existsByCategoryNameAndRestaurantId(String categoryName,Long restaurantId);

    @Query(value = "SELECT c.* FROM category c " +
            "WHERE c.category_name IN (:categoryNames) " +
            "AND c.restaurant_id = :restaurantId",
            nativeQuery = true)
    Set<Category> findByCategoryNamesAndRestaurantId(@Param("categoryNames") Set<String> categoryNames,
                                                     @Param("restaurantId") Long restaurantId);

}
