package org.order_process_system.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerEntity {
    private String PartitionKey;
    private String RowKey;
    private String Timestamp;
    private String customer_id;
    private String address;
    private String email_address;
    private String fullname;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

}
