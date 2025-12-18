# Azure Service Bus – Queue vs Topic (Complete Summary)

#### [Back to Document.md](../Document.md)

## Summary

Azure Service Bus is an **enterprise-grade message broker** used to build **reliable, asynchronous, and decoupled systems**.
This document explains **Service Bus Queue vs Topic & Subscription**, when to use each, naming conventions, and **real Java + Azure Function examples**.

**Key takeaway:**

> **Queue = one message → one consumer**
> **Topic = one message → many consumers (via subscriptions)**

------
<br/>

## Quick Navigation

* [What is Azure Service Bus](#what-is-azure-service-bus)
* [Service Bus Queue](#service-bus-queue)
* [When to Use Queue](#when-to-use-queue)
* [Queue Naming Convention](#queue-naming-convention)
* [Queue Example (Java)](#queue-example-java)
* [Service Bus Topic & Subscription](#service-bus-topic--subscription)
* [When to Use Topic](#when-to-use-topic)
* [Topic & Subscription Naming](#topic--subscription-naming)
* [Topic Example (Java)](#topic-example-java)
* [Queue vs Topic Comparison](#queue-vs-topic-comparison)
* [Decision Rule](#decision-rule)
* [Common Mistakes](#common-mistakes)
* [Real-World Example](#real-world-example-webhook-system)
* [Final Takeaway](#final-takeaway)

------
<br/>

## What is Azure Service Bus

Azure Service Bus enables **asynchronous communication** between services with:

* Guaranteed message delivery
* Retry and dead-letter support
* Message ordering
* Independent scaling of producers and consumers

It is commonly used in **microservices**, **event-driven systems**, and **integration platforms**.

------
<br/>

## Service Bus Queue

### Concept

A **Queue** implements the **point-to-point** messaging pattern.

```
Producer → Queue → Consumer
```

### Characteristics

* Each message is consumed **only once**
* Multiple consumers compete, but **only one processes each message**
* Messages are removed after successful processing

------
<br/>

## When to Use Queue

Use a **Queue** when:

* Only **one system** should process a message
* Tasks must be processed **exactly once**
* You want **load balancing** between workers

### Typical Use Cases

* Order processing
* Email sending
* Payment settlement
* Background jobs

------
<br/>

## Queue Naming Convention

### Pattern

```
<action>-queue
```

### Examples

```
shipment-created-queue
payment-processing-queue
email-send-queue
```

------
<br/>

## Queue Example (Java)

### Producer (Send Message)

```java
//in local.properties.json "TOPIC_NAME":"shipment-created-topic"
@FunctionName("StatsWebhookSender")
public void run(
        @ServiceBusTopicOutput(
                name = "output",
                queueName = "%TOPIC_NAME%",
                connection = "SERVICEBUS_CONNECTION"
        ) OutputBinding<ShopifyWebhookMessage> webhookMessage,
        final ExecutionContext context){}

//OR

ServiceBusSenderClient sender = new ServiceBusClientBuilder()
    .connectionString(SERVICEBUS_CONNECTION)
    .sender()
    .queueName("shipment-created-queue")
    .buildClient();

sender.sendMessage(new ServiceBusMessage("Shipment created"));
```

### Consumer (Azure Function)

```java
@FunctionName("ProcessShipment")
public void run(
    @ServiceBusQueueTrigger(
        name = "message",
        queueName = "shipment-created-queue",
        connection = "SERVICEBUS_CONNECTION"
    ) String message
) {
    System.out.println("Processing shipment: " + message);
}
```

------
<br/>

## Service Bus Topic & Subscription

### Concept

A **Topic** implements the **publish–subscribe** pattern.

```
Producer → Topic → Subscription A → Consumer A
                   Subscription B → Consumer B
```

Each **subscription receives its own copy** of the message.

------
<br/>

## When to Use Topic

Use **Topic + Subscription** when:

* Multiple systems need the **same event**
* Each consumer has **independent logic**
* You need **fan-out** messaging

### Typical Use Cases

* Webhook delivery
* Event-driven microservices
* Audit logging
* Notifications
* Analytics pipelines

------
<br/>

## Topic & Subscription Naming

### Topic Pattern

```
<event>-topic
```

Examples:

```
shipment-created-topic
payment-updated-topic
webhook-topic
```

### Subscription Pattern

```
<consumer>-sub
```

Examples:

```
portal-sub
accounting-sub
retry-worker-sub
analytics-sub
```

---

## Topic Example (Java)

### Producer (Send to Topic)

```java

//in local.properties.json "TOPIC_NAME":"shipment-created-topic"
@FunctionName("StatsWebhookSender")
public void run(
        @ServiceBusTopicOutput(
                name = "output",
                topicName = "%TOPIC_NAME%",
                subscriptionName = "status-feedback-sub",
                connection = "SERVICEBUS_CONNECTION"
        ) OutputBinding<ShopifyWebhookMessage> webhookMessage,
        final ExecutionContext context){}

//OR

ServiceBusSenderClient sender = new ServiceBusClientBuilder()
    .connectionString(SERVICEBUS_CONNECTION)
    .sender()
    .topicName("shipment-created-topic")
    .buildClient();

sender.sendMessage(new ServiceBusMessage("Shipment created"));
```

### Consumer A – Portal

```java
@FunctionName("PortalShipmentHandler")
public void run(
    @ServiceBusTopicTrigger(
        name = "message",
        topicName = "shipment-created-topic",
        subscriptionName = "portal-sub",
        connection = "SERVICEBUS_CONNECTION"
    ) String message
) {
    System.out.println("Portal received: " + message);
}
```

### Consumer B – Accounting

```java
@FunctionName("AccountingShipmentHandler")
public void run(
    @ServiceBusTopicTrigger(
        name = "message",
        topicName = "shipment-created-topic",
        subscriptionName = "accounting-sub",
        connection = "SERVICEBUS_CONNECTION"
    ) String message
) {
    System.out.println("Accounting received: " + message);
}
```

------
<br/>

## Queue vs Topic Comparison

| Feature          | Queue               | Topic + Subscription  |
| ---------------- | ------------------- | --------------------- |
| Pattern          | Point-to-point      | Publish–Subscribe     |
| Message delivery | One consumer        | Multiple consumers    |
| Message copies   | 1                   | 1 per subscription    |
| Scaling          | Competing consumers | Independent consumers |
| Use case         | Tasks, jobs         | Events, notifications |

------
<br/>

## Decision Rule

**Ask one question:**

> Do multiple systems need the same message?

| Answer | Use                  |
| ------ | -------------------- |
| No     | Queue                |
| Yes    | Topic + Subscription |

------
<br/>

## Common Mistakes

* Using **Topic** when only one consumer exists
* Creating multiple queues instead of one topic
* Naming subscriptions generically (e.g. `sub1`, `testSub`)
* Sharing one subscription across different systems

------
<br/>

## Real-World Example (Webhook System)

```
webhook-topic
├─ portal-sub        → Send webhook to portal
├─ retry-worker-sub  → Retry failed webhooks
├─ audit-sub         → Store webhook logs
```

One event → **many independent consumers**.

------
<br/>

## Final Takeaway

* **Queue** = one message, one consumer
* **Topic** = one message, many consumers
* **Subscription = consumer identity**
* Clear naming prevents architectural mistakes
