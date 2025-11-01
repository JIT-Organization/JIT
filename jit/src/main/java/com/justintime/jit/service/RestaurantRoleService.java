package com.justintime.jit.service;

import com.justintime.jit.dto.RestaurantRoleDTO;
import com.justintime.jit.entity.RestaurantRole;

import java.util.List;

public interface RestaurantRoleService extends BaseService<RestaurantRole, Long> {
    List<RestaurantRoleDTO> getRolesByRestaurantCode(String restaurantCode);
    
    RestaurantRoleDTO createRole(RestaurantRoleDTO roleDTO);
    
    RestaurantRoleDTO updateRole(Long id, RestaurantRoleDTO roleDTO);
    
    void deleteRole(Long id);
    
    RestaurantRole getRoleByNameAndRestaurantCode(String name, String restaurantCode);
}
