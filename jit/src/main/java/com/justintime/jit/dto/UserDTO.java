package com.justintime.jit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.justintime.jit.entity.Permissions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    String firstName;
    String lastName;
    String username;
    String phoneNumber;
    String role;
    String email;
    Boolean isActive;
    String shift;
    String profilePictureUrl;
    Set<String> permissionCodes;

    // Only for registration
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Set<String> restaurantCodes;
}
