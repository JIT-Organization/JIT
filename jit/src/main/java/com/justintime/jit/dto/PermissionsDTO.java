package com.justintime.jit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PermissionsDTO {
    private String title;
    private String description;
    private String permissionCode;
    private String role;
}
