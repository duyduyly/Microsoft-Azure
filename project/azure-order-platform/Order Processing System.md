# Order Processing System
## Why use Azure to Process Order?
- No server to manage; Azure runs code on-demand
- Fast to build and cost on Pay only Function Executions (per ms)
- Auto-scales per function call (zero to infinity)

-------------------
<br/>

## Project Overview: 
- A backend system that:
  - Accepts customer orders via HTTP
  - Fetches customer info from Azure Table
  - Publishes the order to a Service Bus Queue for processing
  - Processes the order (e.g., stock check, payment, notification)
- This system is:
  - Scalable (each component can scale independently)
  - Reliable (decoupled with retry & DLQ)
  - Cost-efficient (you pay per execution)

-------------------
<br/>

## Full Architecture Flow
- Download Order Request Function (download order file from sftp)
  - download file from sftp and upload on blobs storages
- OrderReceiverFunction (trigger from Blob storages to get order json)
  - Trigger file and handle
- OrderProcessorFunction (unchanged)
- MailFunction (push success order or  Fail Order from LOG_TOPIC)
  - send mail fail or success for client 
  - send analytic for management

![Flow Order System Image.png](resources/Flow%20Order%20System%20Image.png)

-------------------
<br/>

## Payload

### OrderReceiverFunction

```Json
{
  "customerId": "CUST-9483921",
  "customerEmail": "john.doe@example.com",
  "items": [
    { "productId": "PROD-1001", "quantity": 2 },
    { "productId": "PROD-2042", "quantity": 1 }
  ],
  "shippingAddress": {
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  },
  "notes": "Please deliver between 9 AM and 5 PM."
}

```
#
### OrderProcessorFunction
-  **Success**
```Json
{
        "orderId": "ORD-20250807-0012",
        "customerId": "CUST-9483921",
        "customerEmail": "john.doe@example.com",
        "orderDate": "2025-08-07T11:23:00Z",
        "items": [
        {
        "productId": "PROD-1001",
        "productName": "Wireless Mouse",
        "quantity": 2,
        "price": 25.99
        },
        {
        "productId": "PROD-2042",
        "productName": "Laptop Stand",
        "quantity": 1,
        "price": 45.50
        }
        ],
        "totalAmount": 97.48,
        "shippingAddress": {
        "street": "123 Main Street",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA"
        },
        "paymentStatus": "PENDING",
        "notes": "Please deliver between 9 AM and 5 PM."
        }

```

- do not pass Validate
```Json
{
  "errorType": "ValidationError",
  "errorMessage": "Missing required field: customerEmail",
  "timestamp": "2025-08-07T11:45:00Z",
  "originalPayload": {
    "customerId": "CUST-9483921",
    "items": [
      { "productId": "PROD-1001", "quantity": 2 }
    ],
    "shippingAddress": {
      "street": "123 Main Street",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    }
  }
}

```


-------------------
<br/>



## 📦 Services/Functions You’ll Build
### 1. OrderReceiverFunction
- the first: validate Payload
- Validate quantity in Inventory
- Make a message Json to push in queue

| Type                                     | Purpose                                        |
|------------------------------------------|------------------------------------------------|
| `@FunctionName("OrderReceiverFunction")` | Entry point for receiving orders               |
| `@HttpTrigger`                           | Handles incoming HTTP requests from the client |
| `@TableInput`                            | Retrieves customer info from Table Storage     |
| `@ServiceBusQueueOutput`                 | Pushes validated order to Service Bus queue    |
- ➡️ Input: HTTP request with order JSON
- ➡️ Output: Queue message for further processing

### 2. OrderProcessorFunction
- update Inventory
- Push notification for client

| Type                                      | Purpose                                                |
|-------------------------------------------|--------------------------------------------------------|
| `@FunctionName("OrderProcessorFunction")` | Process incoming order from the queue                  |
| `@ServiceBusQueueTrigger`                 | Triggered when a new order is available in Service Bus |
| (optional) DB/Storage/Email               | Process, update inventory, notify customer, etc.       |
- ➡️ Input: Message from Service Bus
- ➡️ Output: May log, update DB, send email

