package org.order_process_system.func;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.ServiceBusQueueOutput;
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger;
import com.microsoft.azure.functions.annotation.TableInput;
import org.order_process_system.model.payload.OrderPayload;

public class OrderFunction {

    @FunctionName("OrderReceiver")
    public HttpResponseMessage receiveOrder(
            @HttpTrigger(
                    methods = {HttpMethod.POST},
                    name = "req",
                    authLevel = AuthorizationLevel.ANONYMOUS) OrderPayload payload,
            @TableInput(
                    name = "customer",
                    tableName = "CustomerTable",
                    partitionKey = "customerPartition",
                    rowKey = "id",
                    connection = "AzureWebJobsStorage"
            )
            @ServiceBusQueueOutput(
                    name = "in-order-queue",
                    queueName = "order-process-queue",
                    connection = "") OutputBinding<String> output,
            HttpRequestMessage<String> response,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        //validate data
        //set into table input
        //push service bus queue

        String name = "";
        if (name == null) {
            return response.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return response.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("OrderProcessor")
    public void processOrder(
            @ServiceBusQueueTrigger(
                    name = "out-process-queue",
                    queueName = "order-process-queue",
                    connection = "") OrderPayload orderPayload,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        //implement logic
        //check Inventory
        //notification
    }

}
