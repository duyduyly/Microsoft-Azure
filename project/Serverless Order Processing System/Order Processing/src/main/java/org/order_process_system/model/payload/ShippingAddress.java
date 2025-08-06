package org.order_process_system.model.payload;

import lombok.Data;

@Data
public class ShippingAddress {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zip;
    private String country;
}
