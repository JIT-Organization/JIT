package com.justintime.jit.dto;


import com.justintime.jit.util.mapper.AddOnOption;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AddOnDTO {
    private String label;
    private List<AddOnOption> options;
    private BigDecimal price;
    private Set<String> menuItemNames;
}