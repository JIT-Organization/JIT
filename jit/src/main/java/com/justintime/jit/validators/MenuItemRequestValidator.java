package com.justintime.jit.validators;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.util.sanitization.SanitizationUtils;
import jakarta.annotation.Nullable;
import org.modelmapper.ValidationException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.justintime.jit.util.ValidationUtils.runValidation;
import static com.justintime.jit.util.ValidationUtils.shouldValidate;

public class MenuItemRequestValidator implements RequestValidator<MenuItemDTO> {
    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Validates fields of the MenuItemDTO.
     * If fieldsToValidate is null, all fields are validated.
     * Otherwise, only the fields specified in the set are validated.
     */
    @Override
    public void validate(MenuItemDTO dto, @Nullable Set<String> fieldsToValidate, @Nullable String restaurantCode) throws ValidationException {

        List<ErrorMessage> errors = new ArrayList<>();

        // TODO: make this a separate class which contains all the validation rules for all dto present.
        // TODO (Optional): refactor the existing lambda functions into readable code like isMenuItemNameBlank(), isPricePositive()
        // TODO: check if the validations is required for images if we use cdn
        // TODO: add validations for the types in general
        List<ValidationRule> allRules = List.of(
                new ValidationRule("menuItemName", () -> {
                    if (SanitizationUtils.isBlank(dto.getMenuItemName())) {
                        errors.add(new ErrorMessage("Menu Item name cannot be blank or invalid."));
                    } else if (menuItemRepository.existsByMenuItemNameAndRestaurant_RestaurantCode(dto.getMenuItemName(), restaurantCode)) {
                        errors.add(new ErrorMessage("Menu Item exists with this name."));
                    }
                }),

                new ValidationRule("price", () -> {
                    if (Objects.isNull(dto.getPrice()) || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add(new ErrorMessage("Price must be a positive value."));
                    }
                }),

                new ValidationRule("offerPrice", () -> {
                    if (Objects.nonNull(dto.getOfferPrice()) && dto.getOfferPrice().compareTo(BigDecimal.ZERO) < 0) {
                        errors.add(new ErrorMessage("Offer price cannot be negative."));
                    }
                }),

                // TODO: check the dto for the name
                new ValidationRule("offerDateRange", () -> {
                    if (Objects.nonNull(dto.getOfferFrom()) && Objects.nonNull(dto.getOfferTo())
                            && dto.getOfferTo().isBefore(dto.getOfferFrom())) {
                        errors.add(new ErrorMessage("Offer 'to' date cannot be before the 'from' date."));
                    }
                }),

                new ValidationRule("preparationTime", () -> {
                    if (Objects.nonNull(dto.getPreparationTime()) && dto.getPreparationTime() <= 0) {
                        errors.add(new ErrorMessage("Preparation time must be a positive number."));
                    }
                }),

                new ValidationRule("count", () -> {
                    if (Objects.nonNull(dto.getCount()) && dto.getCount() < 0) {
                        errors.add(new ErrorMessage("Menu item count cannot be less than 0."));
                    }
                }),

                new ValidationRule("acceptBulkOrders", () -> {
                    if (Objects.isNull(dto.getAcceptBulkOrders())) {
                        errors.add(new ErrorMessage("'Accept bulk orders' cannot be empty."));
                    }
                }),
                new ValidationRule("onlyVeg", () -> {
                    if (Objects.isNull(dto.getOnlyVeg())) {
                        errors.add(new ErrorMessage("'Only for veg' cannot be empty."));
                    }
                }),
                new ValidationRule("onlyForCombos", () -> {
                    if (Objects.isNull(dto.getOnlyForCombos())) {
                        errors.add(new ErrorMessage("'Only for combos' cannot be empty."));
                    }
                }),
                new ValidationRule("active", () -> {
                    if (Objects.isNull(dto.getActive())) {
                        errors.add(new ErrorMessage("'Active' status cannot be empty."));
                    }
                }),
                new ValidationRule("hotelSpecial", () -> {
                    if (Objects.isNull(dto.getHotelSpecial())) {
                        errors.add(new ErrorMessage("'Hotel special' cannot be empty."));
                    }
                }),

                new ValidationRule("categorySet", () -> {
                    if (Objects.isNull(dto.getCategorySet()) || dto.getCategorySet().isEmpty()) {
                        errors.add(new ErrorMessage("Item must have at least one category."));
                    }
                }),
                new ValidationRule("cookSet", () -> {
                    if (Objects.nonNull(dto.getCookSet()) && dto.getCookSet().isEmpty()) {
                        errors.add(new ErrorMessage("Item must have at least one cook."));
                    }
                }),
                new ValidationRule("timeIntervalSet", () -> {
                    if (Objects.isNull(dto.getTimeIntervalSet()) || dto.getTimeIntervalSet().isEmpty()) {
                        errors.add(new ErrorMessage("Item must have at least one availability period."));
                    }
                })
        );

        runValidation(allRules, fieldsToValidate);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
