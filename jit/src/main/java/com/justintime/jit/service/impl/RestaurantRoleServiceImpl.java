package com.justintime.jit.service.impl;

import com.justintime.jit.dto.RestaurantRoleDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.Permissions;
import com.justintime.jit.entity.RestaurantRole;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.RestaurantRoleRepository;
import com.justintime.jit.service.PermissionsService;
import com.justintime.jit.service.RestaurantRoleService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RestaurantRoleServiceImpl extends BaseServiceImpl<RestaurantRole, Long> implements RestaurantRoleService {

    @Autowired
    private RestaurantRoleRepository restaurantRoleRepository;

    @Autowired
    private PermissionsService permissionsService;

    private final GenericMapper<RestaurantRole, RestaurantRoleDTO> mapper = 
            MapperFactory.getMapper(RestaurantRole.class, RestaurantRoleDTO.class);

    @Override
    public List<RestaurantRoleDTO> getRolesByRestaurantCode(String restaurantCode) {
        List<RestaurantRole> roles = restaurantRoleRepository.findByRestaurantCode(restaurantCode);
        return roles.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RestaurantRoleDTO createRole(RestaurantRoleDTO roleDTO) {
        RestaurantRole role = mapper.toEntity(roleDTO);
        RestaurantRole savedRole = restaurantRoleRepository.save(role);
        return mapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public RestaurantRoleDTO updateRole(Long id, RestaurantRoleDTO roleDTO) {
        RestaurantRole existingRole = restaurantRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant role not found with id: " + id));
        
        if (roleDTO.getRoleType() != null) {
            existingRole.setRoleType(Role.valueOf(roleDTO.getRoleType()));
        }
        existingRole.setName(roleDTO.getName());
        
        // Handle permission codes - parse string to Set and fetch Permissions entities
        if (StringUtils.hasText(roleDTO.getPermissionCodes())) {
            Set<String> permissionCodes = Arrays.stream(roleDTO.getPermissionCodes().split(","))
                    .map(String::trim)
                    .filter(code -> !code.isEmpty())
                    .collect(Collectors.toSet());
            
            if (!permissionCodes.isEmpty()) {
                Set<Permissions> permissions = permissionsService.getAllPermissionsByPermissionCodes(permissionCodes);
                existingRole.setPermissions(permissions);
            }
        }
        
        RestaurantRole updatedRole = restaurantRoleRepository.save(existingRole);
        return mapper.toDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!restaurantRoleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant role not found with id: " + id);
        }
        restaurantRoleRepository.deleteById(id);
    }

    @Override
    public RestaurantRole getRoleByNameAndRestaurantCode(String name, String restaurantCode) {
        return restaurantRoleRepository.findByNameAndRestaurantCode(name, restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role '" + name + "' not found for restaurant: " + restaurantCode));
    }
}
