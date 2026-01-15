package org.order_process_system.service;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import org.order_process_system.model.enums.ValidationEnum;
import org.order_process_system.model.payload.OrderRequest;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ValidationStep {
    private final HttpRequestMessage<Optional<OrderRequest>> request;
    private ValidationEnum validationResult;
    private Supplier<HttpResponseMessage> responseMessageSupplier;
    private final ProcessOrderService processOrderService;
    private static final Logger LOGGER = Logger.getLogger(ValidationStep.class.getName());

    public ValidationStep(HttpRequestMessage<Optional<OrderRequest>> request, ProcessOrderService processOrderService) {
        this.request = request;
        this.validationResult = ValidationEnum.VALID;
        this.processOrderService = processOrderService;
    }

    public static ValidationStep load(HttpRequestMessage<Optional<OrderRequest>> request, ProcessOrderService processOrderService){
       return new ValidationStep(request, processOrderService);
    }

    public ValidationStep validate(){
        LOGGER.info("validate Start");
        if(processOrderService.validateRawPayload(request) == ValidationEnum.PAYLOAD_INVALID){
            LOGGER.info("Payload Invalid");
            validationResult = ValidationEnum.PAYLOAD_INVALID;
        }else if (processOrderService.validateInventory(request.getBody().get()) == ValidationEnum.PRODUCT_INVALID){
            LOGGER.info("Product Invalid");
            validationResult = ValidationEnum.PRODUCT_INVALID;
        }else{
            LOGGER.info("Data Valid");
            validationResult = ValidationEnum.VALID;
        }
        return this;
    }

    public ValidationStep validatePayload(Supplier<HttpResponseMessage> replySupplier){
        if (this.validationResult == ValidationEnum.PAYLOAD_INVALID){
            this.responseMessageSupplier = replySupplier;
        }
        return this;
    }

    public ValidationStep validateProductQuantity(Supplier<HttpResponseMessage> replySupplier){
        if (this.validationResult == ValidationEnum.PRODUCT_INVALID){
            this.responseMessageSupplier = replySupplier;
        }
        return this;
    }

    public ValidationStep onValid(Supplier<HttpResponseMessage> replySupplier){
        if (this.validationResult == ValidationEnum.VALID){
            this.responseMessageSupplier = replySupplier;
        }
        return this;
    }

    public HttpResponseMessage reply() {
        return responseMessageSupplier.get();
    }
}
