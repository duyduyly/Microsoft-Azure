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
```text
[Frontend Client]
     |
     ↓  (POST /order)
[OrderReceiverFunction] ──→ (Lookup Table Storage for customer info)
     ↓
  (ServiceBusQueue: orders)
     ↓
[OrderProcessorFunction]
     ↓
 [Optional: Inventory Check]
     ↓
 [Optional: Send Email/Notification]
```

-------------------
<br/>

## Order Payload
```Json
{
  "orderId": "ORD-20250805-001",
  "customerId": "CUST-789",
  "orderDate": "2025-08-05T10:30:00Z",
  "items": [
    {
      "productId": "PROD-1001",
      "productName": "Wireless Mouse",
      "quantity": 2,
      "unitPrice": 15.99
    },
    {
      "productId": "PROD-1002",
      "productName": "Mechanical Keyboard",
      "quantity": 1,
      "unitPrice": 59.95
    }
  ],
  "shippingAddress": {
    "line1": "123 Azure Street",
    "line2": "Suite 456",
    "city": "Cloudville",
    "state": "WA",
    "zip": "98052",
    "country": "USA"
  },
  "paymentMethod": "VISA",
  "notes": "Please deliver between 9 AM - 12 PM"
}
```

### Describe
| Field             | Type     | Description                                          |
|-------------------|----------|------------------------------------------------------|
| `orderId`         | String   | Unique ID for this order                             |
| `customerId`      | String   | Links to Azure Table row key (partitionKey + rowKey) |
| `orderDate`       | ISO Date | When the order was created                           |
| `items[]`         | Array    | List of products in this order                       |
| `shippingAddress` | Object   | Full shipping address                                |
| `paymentMethod`   | String   | Payment method type                                  |
| `notes`           | String   | Optional delivery notes                              |



-------------------
<br/>

## 📦 Services/Functions You’ll Build
### 1. OrderReceiverFunction
| Type                                     | Purpose                                        |
|------------------------------------------|------------------------------------------------|
| `@FunctionName("OrderReceiverFunction")` | Entry point for receiving orders               |
| `@HttpTrigger`                           | Handles incoming HTTP requests from the client |
| `@TableInput`                            | Retrieves customer info from Table Storage     |
| `@ServiceBusQueueOutput`                 | Pushes validated order to Service Bus queue    |
- ➡️ Input: HTTP request with order JSON
- ➡️ Output: Queue message for further processing

### 2. OrderProcessorFunction
| Type                                      | Purpose                                                |
|-------------------------------------------|--------------------------------------------------------|
| `@FunctionName("OrderProcessorFunction")` | Process incoming order from the queue                  |
| `@ServiceBusQueueTrigger`                 | Triggered when a new order is available in Service Bus |
| (optional) DB/Storage/Email               | Process, update inventory, notify customer, etc.       |
- ➡️ Input: Message from Service Bus
- ➡️ Output: May log, update DB, send email

