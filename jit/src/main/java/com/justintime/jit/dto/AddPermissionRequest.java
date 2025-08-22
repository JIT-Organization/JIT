package com.justintime.jit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AddPermissionRequest {
    private String email;
    private List<PermissionsDTO> permissionsDTOS;
}
