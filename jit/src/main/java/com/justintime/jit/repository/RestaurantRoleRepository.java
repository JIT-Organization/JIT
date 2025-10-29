package com.justintime.jit.repository;

import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.RestaurantRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRoleRepository extends JpaRepository<RestaurantRole, Long> {
    
    List<RestaurantRole> findByRestaurantCode(String restaurantCode);
    
    Optional<RestaurantRole> findByNameAndRestaurantCode(String name, String restaurantCode);
    
    List<RestaurantRole> findByRoleTypeAndRestaurantCode(Role roleType, String restaurantCode);
    
    Optional<RestaurantRole> findFirstByRoleTypeAndRestaurantCode(Role roleType, String restaurantCode);
}
