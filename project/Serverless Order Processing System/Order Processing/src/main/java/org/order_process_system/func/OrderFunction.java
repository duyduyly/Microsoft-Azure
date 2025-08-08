package org.order_process_system.func;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.QueueOutput;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import com.microsoft.azure.functions.annotation.TableInput;
import org.order_process_system.model.entity.CustomerEntity;
import org.order_process_system.model.payload.OrderMessage;
import org.order_process_system.model.payload.OrderRequest;
import org.order_process_system.service.MailService;
import org.order_process_system.service.ProcessOrderService;

import java.util.List;
import java.util.Optional;

public class OrderFunction {
    private final ProcessOrderService orderService = new ProcessOrderService();

    @FunctionName("OrderReceiver")
    public HttpResponseMessage receiveOrder(
            @HttpTrigger(
                    methods = {HttpMethod.POST},
                    name = "req",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<OrderRequest>> request,
            @TableInput(
                    name = "Customer",
                    tableName = "customer",
                    connection = "AzureWebJobsStorage"
            ) List<CustomerEntity> customerEntities,
            @QueueOutput(
                    name = "inqueue",
                    queueName = "order-process-queue",
                    connection = "AzureWebJobsStorage") OutputBinding<String> output,
            final ExecutionContext context) {
        context.getLogger().info("OrderReceiver Start.");
        return orderService.process(request, output);
    }

    @FunctionName("OrderProcessor")
    public void processOrder(
            @QueueTrigger(
                    name = "outqueue",
                    queueName = "order-process-queue",
                    connection = "AzureWebJobsStorage") OrderMessage orderMessage,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        MailService mailService = new MailService();
        //update Inventory
        orderService.updateProductQuantity(orderMessage, context.getLogger());

        //notification
        mailService.push();
    }

}
