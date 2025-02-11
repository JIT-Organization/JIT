package com.justintime.jit.util.mapper;

import com.justintime.jit.dto.ComboDTO;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.TimeInterval;

import java.util.Set;
import java.util.stream.Collectors;

public class ComboMapper {

    public static ComboDTO toDTO(Combo combo) {
        if (combo == null) {
            return null;
        }

        ComboDTO dto = new ComboDTO();
        dto.setId(combo.getId());
        dto.setComboName(combo.getComboName());
        dto.setPrice(combo.getPrice());
        dto.setStock(combo.getStock());
        dto.setDescription(combo.getDescription());
        dto.setOfferPrice(combo.getOfferPrice());
        dto.setOfferFrom(combo.getOfferFrom());
        dto.setOfferTo(combo.getOfferTo());
        dto.setCount(combo.getCount());
        dto.setPreparationTime(combo.getPreparationTime());
        dto.setAcceptBulkOrders(combo.getAcceptBulkOrders());
        dto.setOnlyVeg(combo.getOnlyVeg());
        dto.setActive(combo.getActive());
        dto.setHotelSpecial(combo.getHotelSpecial());
        dto.setBase64Image(combo.getBase64Image());
        dto.setRating(combo.getRating());
        dto.setCreatedDttm(combo.getCreatedDttm());
        dto.setUpdatedDttm(combo.getUpdatedDttm());

        // Map related entity IDs
        if (combo.getRestaurant() != null) {
            dto.setRestaurantId(combo.getRestaurant().getId());
        }

        dto.setComboItemIds(mapComboItemIds(combo.getComboItemSet()));
        dto.setCategoryIds(mapCategoryIds(combo.getCategories()));
        dto.setTimeIntervalIds(mapTimeIntervalIds(combo.getTimeIntervalSet()));

        return dto;
    }

    private static Set<Long> mapComboItemIds(Set<ComboItem> comboItems) {
        return comboItems != null
                ? comboItems.stream().map(ComboItem::getId).collect(Collectors.toSet())
                : null;
    }

    private static Set<Long> mapCategoryIds(Set<Category> categories) {
        return categories != null
                ? categories.stream().map(Category::getId).collect(Collectors.toSet())
                : null;
    }

    private static Set<Long> mapTimeIntervalIds(Set<TimeInterval> timeIntervals) {
        return timeIntervals != null
                ? timeIntervals.stream().map(TimeInterval::getId).collect(Collectors.toSet())
                : null;
    }
}

