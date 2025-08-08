package org.order_process_system.repository;

import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableTransactionAction;
import com.azure.data.tables.models.TableTransactionActionType;
import lombok.extern.slf4j.Slf4j;
import org.order_process_system.model.entity.InventoryEntity;
import org.order_process_system.model.payload.OrderMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class InventoryRepository {
    private final TableClient tableClient;

    public InventoryRepository() {
        String connectionString = System.getenv("AzureWebJobsStorage");
        tableClient = new TableClientBuilder()
                .connectionString(connectionString)
                .tableName("product")
                .buildClient();
    }

    public List<InventoryEntity> findByProductIdListFromMessage(OrderMessage message) {
        List<OrderMessage.Item> items = message.getItems();
        Set<String> productIdSet = items.stream().map(OrderMessage.Item::getProductId).collect(Collectors.toSet());

        String filter = productIdSet.stream()
                .map(id -> String.format("productId eq '%s'", id))
                .collect(Collectors.joining(" or "));

        System.out.printf("Filter: %s", filter);
        return this.queryToMap(filter);
    }

    public List<InventoryEntity> findByProductIdSet(Set<String> productIdSet) {
        String filter = productIdSet.stream()
                .map(id -> String.format("productId eq '%s'", id))
                .collect(Collectors.joining(" or "));

        System.out.printf("Filter: %s", filter);

        return this.queryToMap(filter);
    }

    public void update(List<InventoryEntity> inventoryEntityList) {
        List<TableTransactionAction> actions = new ArrayList<>();

        for (InventoryEntity inventory : inventoryEntityList) {
            TableEntity entity = new TableEntity(inventory.getPartitionKey(), inventory.getRowKey())
                    .addProperty("productId", inventory.getProductId())
                    .addProperty("quantity", inventory.getQuantity());

            actions.add(new TableTransactionAction(TableTransactionActionType.UPDATE_MERGE, entity));
        }

        tableClient.submitTransaction(actions);
    }

    private List<InventoryEntity> queryToMap(String filter) {
        List<TableEntity> results = new ArrayList<>();
        tableClient.listEntities(new ListEntitiesOptions().setFilter(filter), null, null).forEach(results::add);
        return results.stream().map(InventoryEntity::new).toList();
    }
}
