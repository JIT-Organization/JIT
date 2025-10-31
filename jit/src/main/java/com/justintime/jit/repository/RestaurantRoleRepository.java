package com.justintime.jit.repository;

import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.RestaurantRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RestaurantRoleRepository extends JpaRepository<RestaurantRole, Long> {
    
    @Query(value = "SELECT * FROM restaurant_roles", nativeQuery = true)
    List<RestaurantRole> findByRestaurantCode(String restaurantCode);
    
    @Query(value = "SELECT * FROM restaurant_roles WHERE name = :name", nativeQuery = true)
    Optional<RestaurantRole> findByNameAndRestaurantCode(@Param("name") String name, String restaurantCode);
    
    @Query(value = "SELECT * FROM restaurant_roles WHERE role_type = :roleType", nativeQuery = true)
    List<RestaurantRole> findByRoleTypeAndRestaurantCode(@Param("roleType") Role roleType, String restaurantCode);
    
    @Query(value = "SELECT * FROM restaurant_roles WHERE role_type = :roleType LIMIT 1", nativeQuery = true)
    Optional<RestaurantRole> findFirstByRoleTypeAndRestaurantCode(@Param("roleType") Role roleType, String restaurantCode);
}
