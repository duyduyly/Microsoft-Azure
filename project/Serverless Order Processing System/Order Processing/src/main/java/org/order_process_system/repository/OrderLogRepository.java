package org.order_process_system.repository;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.TableEntity;
import org.order_process_system.model.entity.OrderLogEntity;

import java.util.logging.Logger;

public class OrderLogRepository {

    private static final Logger LOGGER = Logger.getLogger(OrderLogRepository.class.getName());
    private final TableClient tableClient;

    public OrderLogRepository() {
        String connectionString = System.getenv("AzureWebJobsStorage");
        tableClient = new TableClientBuilder()
                .connectionString(connectionString)
                .tableName("orderlog")
                .buildClient();
    }

    public void create(OrderLogEntity orderLogEntity) {
        String partitionKey = orderLogEntity.getOrderId() + "_" + orderLogEntity.getOrderDate();
        TableEntity entity = new TableEntity(partitionKey, orderLogEntity.getOrderId())
                .addProperty("total", orderLogEntity.getTotal())
                .addProperty("productIds", orderLogEntity.getProductIds())
                .addProperty("customerId", orderLogEntity.getCustomerId())
                .addProperty("orderDate", orderLogEntity.getOrderDate())
                .addProperty("shipmentAddress", orderLogEntity.getShipmentAddress());
        tableClient.createEntity(entity);
        LOGGER.info("Create order log Success");
    }

}
