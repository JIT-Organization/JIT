package com.justintime.jit.validators.rules;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.util.sanitization.SanitizationUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MenuItemRules extends BaseRules {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @PostConstruct
    public void registerMenuItemRules() {
        registerFieldRules();
        registerBusinessRules();
    }

    private void registerFieldRules() {
        addFieldRule(MenuItemDTO.class, "menuItemName", dto -> SanitizationUtils.isBlank(dto.getMenuItemName()), "Menu Item name cannot be blank");
        addFieldRule(MenuItemDTO.class, "acceptBulkOrders", dto -> dto.getAcceptBulkOrders() == null, "Accept bulk orders cannot be empty");
        addFieldRule(MenuItemDTO.class, "onlyVeg", dto -> dto.getOnlyVeg() == null, "'Only for veg' cannot be empty");
        addFieldRule(MenuItemDTO.class, "onlyForCombos", dto -> dto.getOnlyForCombos() == null, "'Only for combos' cannot be empty");
        addFieldRule(MenuItemDTO.class, "active", dto -> dto.getActive() == null, "'Active' status cannot be empty");
        addFieldRule(MenuItemDTO.class, "hotelSpecial", dto -> dto.getHotelSpecial() == null, "'Hotel special' cannot be empty");
    }

    private void registerBusinessRules() {
        addBusinessRule(MenuItemDTO.class, "menuItemName",
                dto -> menuItemRepository.existsByMenuItemNameAndRestaurant_RestaurantCode(
                        dto.getMenuItemName(), jwtBean.getRestaurantCode()),
                "Menu Item already exists for this restaurant");

        addBusinessRule(MenuItemDTO.class, "price", dto -> dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0,
                "Price must be a positive value");

        addBusinessRule(MenuItemDTO.class, "offerPrice", dto -> dto.getOfferPrice() != null && dto.getOfferPrice().compareTo(BigDecimal.ZERO) < 0,
                "Offer price must be a positive value");

        addBusinessRule(MenuItemDTO.class, "offerDateRange",
                dto -> dto.getOfferPrice() != null && dto.getOfferFrom() != null && dto.getOfferTo() != null
                        && dto.getOfferTo().isBefore(dto.getOfferFrom()),
                "Offer 'to' date cannot be before the 'from' date");

        addBusinessRule(MenuItemDTO.class, "preparationTime", dto -> dto.getPreparationTime() != null && dto.getPreparationTime() <= 0,
                "Preparation time must be a positive value");

        addBusinessRule(MenuItemDTO.class, "count", dto -> dto.getCount() != null && dto.getCount() < 0,
                "Menu item count cannot be less than 0");

        addBusinessRule(MenuItemDTO.class, "categorySet", dto -> dto.getCategorySet() == null || dto.getCategorySet().isEmpty(),
                "Item must have at least one category");

        addBusinessRule(MenuItemDTO.class, "cookSet", dto -> dto.getCookSet() == null || dto.getCookSet().isEmpty(),
                "Item must have at least one cook");

        addBusinessRule(MenuItemDTO.class, "timeIntervalSet", dto -> dto.getTimeIntervalSet() == null || dto.getTimeIntervalSet().isEmpty(),
                "Item must have at least one availability period");
    }
}
