package org.order_process_system.model.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class OrderRequest {

    private String customerId;

    private String customerEmail;
    private List<Item> items;
    private ShippingAddress shippingAddress;
    private String notes;

    @Data
    public static class Item {
        private String productId;
        private int quantity;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShippingAddress {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
    }
}
