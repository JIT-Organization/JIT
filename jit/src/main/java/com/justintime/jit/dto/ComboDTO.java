package com.justintime.jit.dto;

import com.justintime.jit.entity.ComboEntities.ComboItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ComboDTO {
    private Long id;
    private String comboName;
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
    private Boolean active;
    private Boolean hotelSpecial;
    private String base64Image;
    private BigDecimal rating;
    private LocalDateTime createdDttm;
    private LocalDateTime updatedDttm;

    // Related IDs to avoid entity dependencies
    // private Long restaurantId;
    private Set<ComboItemDTO> comboItemSet;
    private Set<String> categorySet;
    private Set<TimeIntervalDTO> timeIntervalSet;
}

