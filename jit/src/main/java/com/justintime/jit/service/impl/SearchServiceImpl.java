package com.justintime.jit.service.impl;

import com.justintime.jit.dto.SearchResultDTO;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public List<SearchResultDTO> searchByName(String query) {
        List<SearchResultDTO> results = new ArrayList<>();

        // Search Restaurants
        List<Restaurant> matchingRestaurants = restaurantRepository.findByRestaurantNameContaining(query);
        for (Restaurant restaurant : matchingRestaurants) {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setType("Restaurant");
            dto.setName(restaurant.getRestaurantName());

            // Get associated foods
            List<String> menuItems = new ArrayList<>();
            restaurant.getMenu().forEach(menuItem -> menuItems.add(menuItem.getMenuItemName()));
            dto.setAssociatedNames(menuItems);

            results.add(dto);
        }

        // Search Foods
        List<MenuItem> matchingMenuItems = menuItemRepository.findByMenuItemNameContaining(query);
        for (MenuItem menuItem : matchingMenuItems) {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setType("MenuItem");
            dto.setName(menuItem.getMenuItemName());

            // Get associated restaurants
            List<String> restaurants = new ArrayList<>();
            restaurants.add(menuItem.getMenuItemName());
            dto.setAssociatedNames(restaurants);

            results.add(dto);
        }

        return results;
    }
    public void checkMenuItemExistsInRestaurant(String restaurantCode, String menuItemName) {
        if (!StringUtils.hasText(menuItemName) || !StringUtils.hasText(restaurantCode)) {
            throw new IllegalArgumentException("Menu item name and restaurant code must not be empty");
        }

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByRestaurantCode(restaurantCode);

        if (restaurantOpt.isEmpty()) {
            throw new IllegalArgumentException("Restaurant with code '" + restaurantCode + "' not found");
        }

        Restaurant restaurant = restaurantOpt.get();
        boolean exists = restaurant.getMenu().stream()
                .anyMatch(menuItem -> menuItem.getMenuItemName().equalsIgnoreCase(menuItemName));

        if (exists) {
            throw new IllegalStateException("Menu item '" + menuItemName + "' already exists in restaurant '" + restaurant.getRestaurantName() + "'");
        }
    }

}
