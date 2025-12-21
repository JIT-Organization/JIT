package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiningTableDTO {
    private Boolean isAvailable;
    private String tableNumber;
    private Integer chairs;
}
