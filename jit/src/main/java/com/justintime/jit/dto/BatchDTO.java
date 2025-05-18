package com.justintime.jit.dto;

import lombok.*;

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
    private Integer currentCount;  // For tracking batch size
    
    // Constructor for virtual batches
    public BatchDTO(BatchConfigDTO batchConfigDTO, List<OrderItemDTO> orderItemsDTO, String status, String batchNumber, Integer currentCount) {
        this.batchConfigDTO = batchConfigDTO;
        this.orderItemsDTO = orderItemsDTO;
        this.status = status;
        this.batchNumber = batchNumber;
        this.currentCount = currentCount;
    }
}
