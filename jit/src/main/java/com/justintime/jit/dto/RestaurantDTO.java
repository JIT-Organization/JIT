package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {
    private String restaurantName;
    private String restaurantCode;
    private String contactNumber;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private Double latitude;
    private Double longitude;
}
