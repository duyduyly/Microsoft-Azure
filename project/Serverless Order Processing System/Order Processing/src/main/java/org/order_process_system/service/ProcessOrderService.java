package org.order_process_system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import lombok.Getter;
import org.order_process_system.model.entity.InventoryEntity;
import org.order_process_system.model.enums.ValidationEnum;
import org.order_process_system.model.payload.OrderMessage;
import org.order_process_system.model.payload.OrderRequest;
import org.order_process_system.repository.InventoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProcessOrderService {
    private final InventoryRepository inventoryRepository = new InventoryRepository();

    @Getter
    private final Map<String, String> errorMessageMap = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();


    public HttpResponseMessage process(HttpRequestMessage<Optional<OrderRequest>> request, OutputBinding<String> orderQueue) {
        return ValidationClass.load(request, this)
                .validate()
                .validatePayload(() ->
                        request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                .body("Payload Invalid")
                                .build()
                )
                .validateProductQuantity(() -> {
                    try {
                        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                .body(objectMapper.writeValueAsString(this.getErrorMessageMap()))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onValid(() -> {
                    try {
                        orderQueue.setValue(this.generateOrderMessage(request.getBody().get()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return request.createResponseBuilder(HttpStatus.ACCEPTED)
                            .body("Order Created Success")
                            .build();

                })
                .reply();
    }

    public ValidationEnum validateRawPayload(HttpRequestMessage<Optional<OrderRequest>> request) {
        Optional<OrderRequest> orderOp = request.getBody();
        if (orderOp.isPresent()) {
            OrderRequest order = orderOp.get();

            if (order.getCustomerEmail().isEmpty()) {
                return ValidationEnum.PAYLOAD_INVALID;
            }
            if (order.getCustomerId().isEmpty()) {
                return ValidationEnum.PAYLOAD_INVALID;
            }
        }

        return ValidationEnum.VALID;
    }

    public ValidationEnum validateInventory(OrderRequest order) {
        List<OrderRequest.Item> items = order.getItems();
        if (items.isEmpty()) {
            return ValidationEnum.PRODUCT_INVALID;
        }

        List<InventoryEntity> bySetProductId = inventoryRepository.findByProductIdSet(items.stream().map(OrderRequest.Item::getProductId).collect(Collectors.toSet()));
        Map<String, InventoryEntity> mapInventoryEntity = bySetProductId.stream().collect(Collectors.toMap(InventoryEntity::getProductId, Function.identity()));

        for (OrderRequest.Item item : items) {
            if (!mapInventoryEntity.containsKey(item.getProductId())) {
                errorMessageMap.put(item.getProductId(), "Product Does Not Exist");
            }

            if (mapInventoryEntity.get(item.getProductId()).getQuantity() < item.getQuantity()) {
                errorMessageMap.put(item.getProductId(), "The stock quantity is insufficient");
            }
        }

        return errorMessageMap.isEmpty() ? ValidationEnum.VALID : ValidationEnum.PRODUCT_INVALID;
    }

    public String generateOrderMessage(OrderRequest order) throws JsonProcessingException {
        Set<String> productIdList = order.getItems().stream().map(OrderRequest.Item::getProductId).collect(Collectors.toSet());
        List<InventoryEntity> bySetProductId = inventoryRepository.findByProductIdSet(productIdList);
        Map<String, InventoryEntity> mapInventoryEntity = bySetProductId.stream().collect(Collectors.toMap(InventoryEntity::getProductId, Function.identity()));

        List<OrderMessage.Item> items = new ArrayList<>();
        double totalAmount = 0D;
        for (OrderRequest.Item item : order.getItems()) {
            InventoryEntity inventoryEntity = mapInventoryEntity.get(item.getProductId());
            items.add(OrderMessage.Item.builder()
                    .productId(inventoryEntity.getProductId())
                    .productName(inventoryEntity.getProductName())
                    .quantity(item.getQuantity())
                    .price(inventoryEntity.getPrice())
                    .build());

            totalAmount += (item.getQuantity() * inventoryEntity.getPrice());
        }
        return objectMapper.writeValueAsString(new OrderMessage(order, items, totalAmount));
    }

    public void updateProductQuantity(OrderMessage order, Logger logger) {
        Map<String, Integer> messageMap = order.getItems().stream().collect(Collectors.toMap(OrderMessage.Item::getProductId, OrderMessage.Item::getQuantity));
        List<InventoryEntity> byProductId = inventoryRepository.findByProductIdListFromMessage(order);

        for (InventoryEntity inventory : byProductId) {
            Integer quantity = messageMap.get(inventory.getProductId());
            inventory.setQuantity(inventory.getQuantity() - quantity);
        }
        inventoryRepository.update(byProductId);
        logger.info("Updated: "+ byProductId);
    }
}