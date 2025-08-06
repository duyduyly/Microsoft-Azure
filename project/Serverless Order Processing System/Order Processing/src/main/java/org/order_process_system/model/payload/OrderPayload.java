package org.order_process_system.model.payload;


import lombok.Data;

import java.util.List;

@Data
public class OrderPayload {
    private String orderId;
    private String customerId;
    private String orderDate;
    private List<Item> items;
    private ShippingAddress shippingAddress;
    private String paymentMethod;
    private String notes;
}
