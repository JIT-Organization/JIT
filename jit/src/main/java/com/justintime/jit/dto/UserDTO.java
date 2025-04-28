package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
