package com.justintime.jit.util.filter;

import com.justintime.jit.entity.Enums.FoodType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FilterableItem {
    Long getId();
    String getName();
    BigDecimal getPrice();
    LocalDateTime getUpdatedDttm();
    BigDecimal getRating();
    Boolean getOnlyForCombos();
    Boolean getOnlyVeg();
    FoodType getFoodType();
}
