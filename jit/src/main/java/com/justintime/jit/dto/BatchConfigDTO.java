package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchConfigDTO {
    private Long id;
    private String batchConfigName;
    private String batchConfigNumber;
    private String maxCount;
    private Integer estdBatchPrepTime;
    private List<String> menuItemNames;  // Store only the names of menu items
    private List<String> batchNumbers; 
}
