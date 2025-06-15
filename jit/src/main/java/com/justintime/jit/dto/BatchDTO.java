package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 Only for kitchen purpose
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDTO {
    private BatchConfigDTO batchConfigDTO;
    private List<OrderItemDTO> orderItemsDTO;
    private String status;
    private String batchNumber;
    private Integer currentCount;
}
