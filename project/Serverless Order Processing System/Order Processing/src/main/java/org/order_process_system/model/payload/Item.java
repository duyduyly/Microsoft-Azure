package org.order_process_system.model.payload;

import lombok.Data;

@Data
public class Item {
    private String productId;
    private String productName;
    private int quantity;
    private String unitPrice;
}
