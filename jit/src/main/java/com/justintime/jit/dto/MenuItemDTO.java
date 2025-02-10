package com.justintime.jit.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class MenuItemDTO {
    private Long id;
    private String menuItemName;
    private Long restaurantId;
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
    private Set<Long> categoryIds;
    private Set<Long> cookIds;
    private Set<Long> timeIntervalIds;
}

