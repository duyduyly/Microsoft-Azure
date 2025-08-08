package org.order_process_system.service;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import org.order_process_system.model.enums.ValidationEnum;
import org.order_process_system.model.payload.OrderRequest;

import java.util.Optional;
import java.util.function.Supplier;

public class ValidationClass {
    private final HttpRequestMessage<Optional<OrderRequest>> request;
    private ValidationEnum validationResult;
    private Supplier<HttpResponseMessage> responseMessageSupplier;

    private final ProcessOrderService processOrderService;

    public ValidationClass(HttpRequestMessage<Optional<OrderRequest>> request, ProcessOrderService processOrderService) {
        this.request = request;
        this.validationResult = ValidationEnum.UNKNOWN;
        this.processOrderService = processOrderService;
    }

    public static ValidationClass load(HttpRequestMessage<Optional<OrderRequest>> request, ProcessOrderService processOrderService){
       return new ValidationClass(request, processOrderService);
    }

    public ValidationClass validate(){
        if(processOrderService.validateRawPayload(request) == ValidationEnum.PAYLOAD_INVALID){
            validationResult = ValidationEnum.PAYLOAD_INVALID;
        }else if (processOrderService.validateInventory(request.getBody().get()) == ValidationEnum.PRODUCT_INVALID){
            validationResult = ValidationEnum.PRODUCT_INVALID;
        }else{
            validationResult = ValidationEnum.VALID;
        }
        return this;
    }

    public ValidationClass validatePayload(Supplier<HttpResponseMessage> replySupplier){
        if (this.validationResult == ValidationEnum.PAYLOAD_INVALID){
            this.responseMessageSupplier = replySupplier;
        }
        return this;
    }

    public ValidationClass validateProductQuantity(Supplier<HttpResponseMessage> replySupplier){
        if (this.validationResult == ValidationEnum.PRODUCT_INVALID){
            this.responseMessageSupplier = replySupplier;
        }
        return this;
    }

    public ValidationClass onValid(Supplier<HttpResponseMessage> replySupplier){
        if (this.validationResult == ValidationEnum.VALID){
            this.responseMessageSupplier = replySupplier;
        }
        return this;
    }

    public HttpResponseMessage reply() {
        return responseMessageSupplier.get();
    }
}
