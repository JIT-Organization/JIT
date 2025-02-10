package com.justintime.jit.util.Mapper;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.entity.MenuItem;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Cook;
import com.justintime.jit.entity.TimeInterval;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MenuItemMapper {
    public static MenuItemDTO toDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setMenuItemName(menuItem.getMenuItemName());
        dto.setRestaurantId(menuItem.getRestaurant().getId());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setOfferPrice(menuItem.getOfferPrice());
        dto.setOfferFrom(menuItem.getOfferFrom());
        dto.setOfferTo(menuItem.getOfferTo());
        dto.setStock(menuItem.getStock());
        dto.setCount(menuItem.getCount());
        dto.setPreparationTime(menuItem.getPreparationTime());
        dto.setAcceptBulkOrders(menuItem.getAcceptBulkOrders());
        dto.setOnlyVeg(menuItem.getOnlyVeg());
        dto.setOnlyForCombos(menuItem.getOnlyForCombos());
        dto.setActive(menuItem.getActive());
        dto.setHotelSpecial(menuItem.getHotelSpecial());
        dto.setBase64Image(menuItem.getBase64Image());
        dto.setRating(menuItem.getRating());
        dto.setCreatedDttm(menuItem.getCreatedDttm());
        dto.setUpdatedDttm(menuItem.getUpdatedDttm());

        // Extract category IDs (assuming a relationship with Category)
        Set<Long> categoryIds = Optional.ofNullable(menuItem.getCategories())
                .orElse(Collections.emptySet())  // Prevent NullPointerException
                .stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        dto.setCategoryIds(categoryIds);


        // Extract cook IDs
        dto.setCookIds(menuItem.getCookSet().stream()
                .map(Cook::getId)
                .collect(Collectors.toSet()));

        // Extract time interval IDs
        dto.setTimeIntervalIds(menuItem.getTimeIntervalSet().stream()
                .map(TimeInterval::getId)
                .collect(Collectors.toSet()));

        return dto;
    }
}

