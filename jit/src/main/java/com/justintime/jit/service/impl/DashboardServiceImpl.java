package com.justintime.jit.service.impl;

import com.justintime.jit.dto.DashboardDTO;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.entity.RestaurantRole;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.*;
import com.justintime.jit.repository.ComboRepo.ComboItemRepository;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ComboItemRepository comboItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private AddOnRepository addOnRepository;

    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestaurantRoleRepository restaurantRoleRepository;


    public DashboardDTO getDashboardData(String restaurantCode) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode).orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with restaurant code" + restaurantCode));
        DashboardDTO dashboardDTO = new DashboardDTO();
        
        // Get role IDs for this restaurant by roleType
        RestaurantRole cookRole = restaurantRoleRepository.findFirstByRoleTypeAndRestaurantCode(Role.COOK, restaurantCode)
                .orElse(null);
        RestaurantRole serverRole = restaurantRoleRepository.findFirstByRoleTypeAndRestaurantCode(Role.SERVER, restaurantCode)
                .orElse(null);
        RestaurantRole customerRole = restaurantRoleRepository.findFirstByRoleTypeAndRestaurantCode(Role.CUSTOMER, restaurantCode)
                .orElse(null);
        
        dashboardDTO.setTotalMenuItems(menuItemRepository.countByRestaurantId(restaurant.getId()));
        dashboardDTO.setTotalCombos(comboRepository.countByRestaurantId(restaurant.getId()));
        dashboardDTO.setTotalCooks(cookRole != null ? userRepository.countByRestaurantIdAndRole(restaurant.getId(), cookRole.getId()) : 0);
        dashboardDTO.setTotalServers(serverRole != null ? userRepository.countByRestaurantIdAndRole(restaurant.getId(), serverRole.getId()) : 0);
        dashboardDTO.setTotalOrders(orderRepository.countByRestaurantId(restaurant.getId()));
        dashboardDTO.setTotalDiningTables(diningTableRepository.countByRestaurantId(restaurant.getId()));
        dashboardDTO.setTotalAddOns(addOnRepository.countByRestaurantId(restaurant.getId()));
        dashboardDTO.setTotalCategories(categoryRepository.countByRestaurantId(restaurant.getId()));
        dashboardDTO.setTotalCustomers(customerRole != null ? userRepository.countByRestaurantIdAndRole(restaurant.getId(), customerRole.getId()) : 0);
        dashboardDTO.setTotalRevenue(orderRepository.calculateTotalRevenueByRestaurantId(restaurant.getId()));
        dashboardDTO.setTotalItems(dashboardDTO.getTotalMenuItems() + dashboardDTO.getTotalCombos());
        return dashboardDTO;
    }
}
