package com.justintime.jit.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
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
    private Set<TimeIntervalDTO> timeIntervalSet;
    private Set<DayOfWeek> availability;
    private String batchConfigNumber;
    private Set<String> cookSet;
}

