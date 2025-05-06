package com.justintime.jit.dto;

import com.justintime.jit.entity.KitchenSet;
import com.justintime.jit.entity.OrderEntities.OrderItem;
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
    KitchenSet kitchenSet;
    List<OrderItem> orderItems;
    String status;
}
