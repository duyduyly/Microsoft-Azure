package org.order_process_system.func;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.ServiceBusQueueOutput;
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger;
import org.order_process_system.constant.AppConstant;
import org.order_process_system.model.payload.OrderMessage;
import org.order_process_system.model.payload.OrderRequest;
import org.order_process_system.service.MailService;
import org.order_process_system.service.ProcessOrderService;

import java.util.Optional;

public class OrderFunction {
    @FunctionName("OrderReceiver")
    public HttpResponseMessage receiveOrder(
            @HttpTrigger(
                    methods = {HttpMethod.POST},
                    name = "req",
                    authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<OrderRequest>> request,
            @ServiceBusQueueOutput(
                    name = "enqueue",
                    queueName = AppConstant.ORDER_QUEUE_KEY,
                    connection = "orderServiceBus-connectString"
            ) OutputBinding<OrderMessage> output,
            final ExecutionContext context) {
        context.getLogger().info("OrderReceiver Start with raw Payload: " + request.getBody().get());
        ProcessOrderService orderService = new ProcessOrderService();
        return orderService.process(request, output);
    }

    @FunctionName("OrderProcessor")
    public void processOrder(
            @ServiceBusQueueTrigger(
                    name = "dequeue",
                    queueName = AppConstant.ORDER_QUEUE_KEY,
                    connection = "orderServiceBus-connectString") OrderMessage orderMessage,
            final ExecutionContext context) {

        context.getLogger().info("OrderProcessor Start With Message: " + orderMessage.toString());
        ProcessOrderService orderService = new ProcessOrderService();
        MailService mailService = new MailService();

        //update Inventory
        orderService.updateProductQuantity(orderMessage);

        //create Order Log
        orderService.createOrderLog(orderMessage);

        //notification
        mailService.push();
    }

}
