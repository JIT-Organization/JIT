package com.justintime.jit.dto;

import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Cook;
import com.justintime.jit.entity.TimeInterval;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class MenuItemDTO {
    private String menuItemName;
    private String description;
    private BigDecimal price;
    private BigDecimal offerPrice;
    private LocalDateTime offerFrom;
    private LocalDateTime offerTo;
    private Integer stock;
    private Integer count;
    private Integer preparationTime;
    private Boolean acceptBulkOrders;
    private Boolean onlyVeg;
    private Boolean onlyForCombos;
    private Boolean active;
    private Boolean hotelSpecial;
    private String base64Image;
    private BigDecimal rating;
    private LocalDateTime createdDttm;
    private LocalDateTime updatedDttm;
    private Set<String> categorySet;
    private Set<String> cookSet;
    private Set<TimeIntervalDTO> timeIntervalSet;
    private String kitchenSetNumber;
}

