package org.order_process_system.model.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.order_process_system.model.enums.PaymentEnum;
import org.order_process_system.utils.DateUtils;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderMessage {
    private String orderId;
    private String customerId;
    private String customerEmail;
    private String orderDate;
    private String paymentStatus;
    private String notes;
    private Double totalAmount;
    private List<Item> items;
    private ShippingAddress shippingAddress;

    public OrderMessage(OrderRequest order, List<Item> items, Double totalAmount){
        this.orderId = "OD-" + System.currentTimeMillis();
        this.orderDate = DateUtils.getCurrentStrDate();
        this.customerId = order.getCustomerId();
        this.customerEmail = order.getCustomerEmail();
        this.items = items;
        this.shippingAddress = new ShippingAddress(order.getShippingAddress());
        this.totalAmount = totalAmount;
        this.notes = order.getNotes();
        this.paymentStatus = PaymentEnum.PENDING.name();
    }

    @Data
    @Builder
    public static class Item{
        private String productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddress {
        private String street;
        private String city;
        private String state;
        private String zipCode;
//        private String country;

        public ShippingAddress(OrderRequest.ShippingAddress shippingAddress){
            this.street = shippingAddress.getStreet();
            this.city = shippingAddress.getCity();
            this.state = shippingAddress.getState();
            this.zipCode = shippingAddress.getZipCode();
        }
    }
}
