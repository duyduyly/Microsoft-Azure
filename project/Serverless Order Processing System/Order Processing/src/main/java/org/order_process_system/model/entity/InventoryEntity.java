package org.order_process_system.model.entity;

import com.azure.data.tables.models.TableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEntity {
    private String PartitionKey;
    private String RowKey;
    private String Timestamp;
    private String productId;
    private String productName;
    private Integer quantity;
    private Double price;

    public InventoryEntity(TableEntity tableEntity){
        this.PartitionKey = tableEntity.getPartitionKey();
        this.RowKey = tableEntity.getRowKey();
        this.Timestamp = String.valueOf(tableEntity.getTimestamp());
        this.productId = String.valueOf(tableEntity.getProperties().get("productId"));
        this.productName = String.valueOf(tableEntity.getProperties().get("productName"));
        this.quantity = Integer.valueOf(String.valueOf(tableEntity.getProperties().get("quantity")));
        this.price = Double.valueOf(String.valueOf(tableEntity.getProperties().get("price")));
    }
}
