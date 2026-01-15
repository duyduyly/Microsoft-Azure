package org.order_process_system.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLogEntity {
    private String orderId;
    private Double total;
    private String productIds;
    private String customerId;
    private String orderDate;
    private String shipmentAddress;
}
