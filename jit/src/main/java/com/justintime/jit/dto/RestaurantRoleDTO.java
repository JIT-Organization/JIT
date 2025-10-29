package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRoleDTO {
    private Long id;
    private String roleType;  // ADMIN, COOK, SERVER, CUSTOMER
    private String name;
    private String permissionCodes;
    private String restaurantCode;
}
