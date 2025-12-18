# Azure Naming Convention – Complete Guide

#### [Back To Home Document](../Document.md)

## Summary

This document defines a **clear, consistent, and scalable naming convention** for **all Azure resources**.
The goal is to ensure:

* Easy identification of **project, environment, and purpose**
* Safe operations (avoid deleting wrong resources)
* Better governance, cost tracking, and team collaboration

**Golden Rule:**

> Azure naming = `project + environment + purpose`
> Use **lowercase**, **hyphen (-)**, and keep names **short but meaningful**.

---

## Quick Navigation

* [Global Naming Rules](#global-naming-rules)
* [Resource Group](#resource-group)
* [Azure Function App](#azure-function-app)
* [Azure Functions (Code Level)](#azure-functions-code-level)
* [HTTP Routes](#http-routes)
* [Storage Account](#storage-account)
* [Blob Container](#blob-container)
* [Storage Table](#storage-table)
* [PartitionKey & RowKey](#partitionkey--rowkey)
* [Service Bus](#service-bus)
* [Application Insights](#application-insights)
* [Key Vault](#key-vault)
* [Key Vault Secrets](#key-vault-secrets)
* [Environment Variables](#environment-variables)
* [Managed Identity](#managed-identity)
* [Region Naming Rules](#region-naming-rules)
* [Full Real-World Example](#full-real-world-example)

-------
<br/>

## Global Naming Rules

Applies to **all Azure resources** unless stated otherwise.

* Use **lowercase**
* Use **hyphen (-)** as separator
* Do **NOT** use spaces or underscores `_`
* Names must clearly indicate:

    * Project
    * Environment (`dev`, `uat`, `prod`)
    * Purpose

-------
<br/>

## Resource Group

### Pattern

```
rg-<project>-<env>-<purpose>
```

### Examples

```
rg-shipment-dev-core
rg-shipment-prod-webhook
rg-payment-prod-integration
```

-------
<br/>

## Azure Function App

### Pattern

```
<project>-<env>-func
```

### Examples

```
shipment-dev-func
shipment-prod-func
```

-------
<br/>

## Azure Functions (Code Level)

### Pattern

```
<Verb><Object>
```

### Examples

```text
CreateShipment
HandleWebhook
ProcessPayment
RetryFailedMessage
```

-------
<br/>

## HTTP Routes

### Pattern

```
/api/<resource>/<action>
```

### Examples

```text
POST /api/webhooks/shipments
POST /api/payments/confirm
GET  /api/orders/{orderId}

POST /api/webhooks/shipments-create
GET  /api/orders-orderdetails
```

-------
<br/>

## Storage Account

### Rules (Azure enforced)

* 3–24 characters
* Lowercase letters and numbers only
* No hyphens

### Pattern

```text
<project><env>st
```

### Examples

```text
shipmentdevst
shipmentprodst
```

-------
<br/>

## Blob Container

### Pattern

```text
<resource>-<purpose>
```

### Examples

```text
shipment-files
invoice-pdf
logs-archive
```

-------
<br/>

## Storage Table

### Pattern

```text
<Entity><Purpose>Table
```

### Examples

```text
WebhookConfigTable
ShipmentStatusTable
PaymentLogTable
```

-------
<br/>

## PartitionKey & RowKey

### Best Practices

| Key          | Recommended Values              |
| ------------ | ------------------------------- |
| PartitionKey | accountId, tenantId, merchantId |
| RowKey       | businessId, uuid, timestamp     |

### Example

```text
PartitionKey = accountId
RowKey       = shipmentId
```

-------
<br/>

## Service Bus

### Namespace

```text
<project>-<env>-sb
```
Examples:

```text
shipment-prod-sb
payment-dev-sb
```

### Topic / Queue

```text
kebab-case
```

Examples:

```text
webhook-topic
shipment-created
payment-failed
```

### Subscription

```text
<consumer>-sub
```

Examples:

```text
portal-sub
retry-worker-sub
```

-------
<br/>

## Application Insights

### Pattern

```text
appinsights-<project>-<env>
```

### Examples

```text
appinsights-shipment-dev
appinsights-shipment-prod
```

-------
<br/>

## Key Vault

### Pattern

```text
<project>-<env>-kv
```

### Examples

```text
shipment-prod-kv
payment-dev-kv
```

-------
<br/>

## Key Vault Secrets

### Pattern

```text
lowercase-with-hyphen
```

### Examples

```text
servicebus-connection
webhook-secret-key
storage-connection
```

-------
<br/>

## Environment Variables

### Pattern

```text
UPPER_CASE_WITH_UNDERSCORE
```

### Examples

```text
AZURE_WEBJOBS_STORAGE
SERVICEBUS_CONNECTION
WEBHOOK_SECRET_KEY
```

-------
<br/>

## Managed Identity

### Pattern

```text
mi-<project>-<env>-<purpose>
```

### Examples

```text
mi-shipment-prod-func
mi-payment-dev-worker
```

-------
<br/>

## Region Naming Rules

### When to Include Region

Only include region **when multiple identical resources exist in different regions**.

### Region Abbreviations

| Region         | Code |
| -------------- | ---- |
| Southeast Asia | sea  |
| East US        | eus  |
| West Europe    | weu  |

### Example

```text
shipment-prod-func-sea
shipmentprodstsea
```

-------
<br/>

## Full Real-World Example

```text
rg-shipment-prod-webhook
├─ shipment-prod-func
├─ shipmentprodst
├─ shipment-prod-sb
│   ├─ webhook-topic
│   │   └─ portal-sub
├─ appinsights-shipment-prod
├─ shipment-prod-kv
│   └─ webhook-secret-key
├─ WebhookConfigTable
```

-------
<br/>

## Final Reminder

> **Good naming prevents production accidents.**
> If you can understand the resource purpose by name alone, your naming is correct.
