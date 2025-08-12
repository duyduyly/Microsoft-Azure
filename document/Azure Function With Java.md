# Azure Function With Java
- [**All Annotation**](#all-annotation)
- [*@TableInput and @TableOutput*](#tableinput-and-tableoutput)
- [**@QueueTrigger And @QueueOutput**](#)
- [**@ServiceBusQueueTrigger vs @ServiceBusQueueOutput**](#servicebusqueuetrigger-vs-servicebusqueueoutput)
  - [*What is Service Bus?*](#what-is-service-bus-queue-in-azure)
  - [*Create On Azure*](#create-on-azure)
  - [*Setup On Local*](#setup-on-local)
  - [*Create Programmatically Via Java SDK*](#create-programmatically-via-java-sdk)
  - [*Service Bus with Topic and Subscriptions*](#service-bus-with-topic-and-subscription-only-support-standardpremium)
- [**Blob Storage**](#blob-storage)
- [**@TimerTrigger (scheduler Job)**](#timertrigger-scheduler-job)

------------------------
<br/>

## All Annotation
- Azure Functions in Java provides a set of annotations to define input and output bindings, triggers, and other configurations directly in your Java code. These annotations simplify the process of creating serverless functions. Below is a list of commonly used annotations in Azure Functions for Java:

### 1. Trigger Annotations
| **Annotation**            | **Using**                                                               |
|---------------------------|-------------------------------------------------------------------------|
| `@FunctionName`           | Specifies the name of the Azure Function                                |
| `@HttpTrigger`            | Defines an HTTP trigger for the function                                |
| `@BlobTrigger`            | Triggers the function when a blob is created or updated in Blob Storage |
| `@QueueTrigger`           | Triggers the function when a message is added to a Storage Queue        |
| `@EventHubTrigger`        | Triggers the function when an event is received from Event Hub          |
| `@ServiceBusQueueTrigger` | Triggers the function when a message is added to a Service Bus Queue    |
| `@ServiceBusTopicTrigger` | Triggers the function when a message is added to a Service Bus Topic    |
| `@TimerTrigger`           | Triggers the function based on a timer schedule                         |

### 2. Input Binding Annotations
| **Annotation**   | **Using**                                              |
|------------------|--------------------------------------------------------|
| `@BlobInput`     | Binds a blob from Azure Blob Storage as input          |
| `@QueueInput`    | Binds a message from an Azure Storage Queue as input   |
| `@TableInput`    | Binds data from an Azure Table Storage entity as input |
| `@CosmosDBInput` | Binds data from Azure Cosmos DB as input               |

### 3. Output Binding Annotations
| **Annotation**    | **Using**                                      |
|-------------------|------------------------------------------------|
| `@BlobOutput`     | Writes output to Azure Blob Storage            |
| `@QueueOutput`    | Writes output to an Azure Storage Queue        |
| `@TableOutput`    | Writes output to an Azure Table Storage entity |
| `@CosmosDBOutput` | Writes output to Azure Cosmos DB               |

### 4.  Other Useful Annotations
| **Annotation**    | **Using**                                                                |
|-------------------|--------------------------------------------------------------------------|
| `@BindingName`    | Used to bind a specific parameter name from the trigger or input binding |
| `@Cardinality`    | Specifies the cardinality of the input (e.g., single or multiple items)  |
| `@StorageAccount` | Specifies the Azure Storage account to use for bindings                  |

--------------------------
<br/>

## @TableInput and @TableOutput
### @Table Input
- `@TableInput` Connect To Table On Azure and get Value From

```java
import com.microsoft.azure.functions.annotation.TableInput;

   @TableInput(name = "customer", 
                tableName = "CustomerTable", 
                partitionKey = "customerPartition", 
                rowKey = "{id}", 
                connection = "AzureWebJobsStorage") CustomerEntity customerEntity
)
```

#
### Parameters
- it is commonly used in Azure Functions when working with Azure Table Storage bindings in Java.
- Use @TableInput when you want to automatically bind to a specific row in Azure Table Storage inside an Azure Function.

| Field          | Description                                                               |
|----------------|---------------------------------------------------------------------------|
| `name`         | Variable name for the binding                                             |
| `tableName`    | Name of the Table in Azure Storage                                        |
| `partitionKey` | Partition key of the row you want to retrieve                             |
| `rowKey`       | Row key (can use `{}` syntax to map from request params)                  |
| `connection`   | Name of the app setting containing the connection string to Table Storage |

#
### Create Azure table with code
- `@TableInput` Can not create table because it's only to read data from an already existing table in Azure Table Storage.
- If you want to create you must use:

```java
import com.azure.data.tables.*;
import com.azure.data.tables.models.*;

public class TableHelper {
    public static void createTableIfNotExists(String tableName, String connectionString) {
        TableServiceClient serviceClient = new TableServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();

        if (!serviceClient.listTableNames().stream().anyMatch(name -> name.equalsIgnoreCase(tableName))) {
            serviceClient.createTable(tableName);
            System.out.println("Table created: " + tableName);
        } else {
            System.out.println("Table already exists: " + tableName);
        }
    }
}
```

------------------------
<br/>


## @ServiceBusQueueTrigger vs @ServiceBusQueueOutput

#
### What is Service Bus Queue in Azure?
- Service Bus Queue is a message broker component that lets you send and receive messages in a decoupled, reliable, and asynchronous way.

**Comparative Cost Scenarios (Monthly Estimates) (Prising):**

| Scenario                      | Basic (\$) | Standard (\$) | Premium (\$) |
|-------------------------------|------------|---------------|--------------|
| 1M messages/month             | \~0.10     | \~9.72        | \~668.16     |
| 1M messages/day (30M/month)   | \~3        | \~47.32       | \~668.16     |
| 1M messages/hour (720M/month) | \~72       | \~749.32      | \~668.16     |
- `Basic`: Very low cost, ideal for development or low-volume use.
- `Standard`: Balances cost and features; best for moderate workloads and production with occasional spikes.
- `Premium`: For high-throughput, low-latency, or mission-critical systems; pricing is flat-per-hour, offering stability and advanced features.

#
### Create On Azure
- Search Service Bus in Azure -> Create -> fill form by requirement -> can create Queue
- ![Create Service Bus Form.png](resource/service-bus-img/Create%20Service%20Bus%20Form.png)
- ![Create Queue Form.png](resource/service-bus-img/Create%20Queue%20Form.png)
- Step Config Service bus Queue Into your Function Service:
  - `Your Service bus` -> `Settings` -> `Shared access policies` -> `RootManageSharedAccessKey` -> copy `Primary connection string`
- Step Create variable for Function Service: 
  - `Your Function Service` -> `Settings` -> `Environment variables` -> `Add` -> `name` (connection in Code) and `value` (is above link (`Primary connection string`))
- So we had connection of @ServiceBus next step we will set up on local

#
### Setup on local
| Annotation                | Purpose                     | Direction | Use Case                                |
|---------------------------|-----------------------------|-----------|-----------------------------------------|
| `@ServiceBusQueueTrigger` | Reads messages from a queue | Input     | Trigger the function on message arrival |
| `@ServiceBusQueueOutput`  | Sends message to a queue    | Output    | Output data to another queue            |

- `connection` is name just config above
- `queueName` is name of queue in Service Bus Queue 
  - `%order-queue%` use value in `settings`
    - on Azure is value in `Environment variables`
    - on Local is value in `local.settings.json`
- `name` is name of Annotation

````java
@ServiceBusQueueOutput(
        name = "enqueue",
        queueName = "%order-queue%", // use value in local.settings.json
        connection = "orderServiceBus-connectString") OutputBinding<OrderMessage> output;
@ServiceBusQueueTrigger(
        name = "dequeue",
        queueName = "%order-queue%",
        connection = "orderServiceBus-connectString") OrderMessage orderMessage;
````

**local.settings.json**
```json
{
        "IsEncrypted": false,
        "Values": {
        "AzureWebJobsStorage": "UseDevelopmentStorage=true",
        "FUNCTIONS_WORKER_RUNTIME": "java",
        "orderServiceBus-connectString": "Endpoint=sb://<name-space>.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=privateKey"
        ,"order-queue": "order-process-queue" 
    }
}
```
![Environment Variable.png](resource/service-bus-img/Environment%20Variable.png)

#
### Create Programmatically via Java SDK
```pom
<dependency>
    <groupId>com.microsoft.azure.functions</groupId>
    <artifactId>azure-functions-java-library</artifactId>
    <version>1.4.2</version>
</dependency>
<dependency>
    <groupId>com.azure</groupId>
    <artifactId>azure-messaging-servicebus</artifactId>
    <version>7.17.0</version>
</dependency>
```

```java
import com.azure.messaging.servicebus.administration.*;

public class QueueSetup {
    public static void createQueueIfNotExists(String connectionString, String queueName) {
        ServiceBusAdministrationClient adminClient = new ServiceBusAdministrationClientBuilder()
            .connectionString(connectionString)
            .buildClient();

        if (!adminClient.getQueueExists(queueName)) {
            adminClient.createQueue(queueName);
            System.out.println("Queue created: " + queueName);
        } else {
            System.out.println("Queue already exists: " + queueName);
        }
    }
}
```

#
### Service Bus With Topic and Subscription (Only Support Standard/Premium)
- In a topic, you can create multiple Subscription it's same queue, but why need use topic-subscriptions?:
  - Problem: With queues, a message is consumed by only one receiver. If multiple systems need the same message, you must send it multiple times (manual fan-out).
  - Solution: Topic + subscriptions let you:
    - Send once
    - Let all interested subscribers get their own cop
    - No producer changes if new consumers are added later
      - if you use basic you need create extra queue and change Function TriggerOutput
      - but with topic you don't need 

- `topicName`: the name of your topic in Service Bus.
- `subscriptionName`: the name of the subscription under that topic.

#### Create:
- Create ServiceBus Standard Or Premium
- ![Create Topic.png](resource/service-bus-img/Create%20Topic.png)
- And create `Subscription` ![Create Subscription.png](resource/service-bus-img/Create%20Subscription.png)

#### Prising

- On Reddit, one user highlighted that despite occasional infrastructure hiccups, Standard works well at a nominal cost—even with 30 million messages per month.

| Architecture                             | Operations per Message   | Monthly Cost Estimate                    | Notes                                         |
|------------------------------------------|--------------------------|------------------------------------------|-----------------------------------------------|
| **3 Queues (Basic Tier)**                | 6 (3 sends + 3 receives) | \~\$0.30 (1M msgs × \$0.05)              | No base fee; scales with number of operations |
| **1 Topic + 3 Subscriptions (Standard)** | 4 (1 send + 3 receives)  | \~\$10 base, possibly more if over limit | Efficient; built-in duplication; feature rich |


- Prising when use

| Component                       | Cost Estimate                                |
|---------------------------------|----------------------------------------------|
| **Base Fee (Standard Tier)**    | \~\$9.72/month                               |
| **Included Operations**         | 13M operations free per month                |
| **Additional Ops**              | \$5.21 → \$1.27 per million (tiered rates)   |
| **Example (1M msgs via topic)** | \~\$9.72 total (operations within free tier) |



#### Example:
- with example: 1 Trigger Output for three queue
  - When use `Queue`, you must call `3 queue` in `ServiceBusQueueOutput` and `call 3 times`
- With example under use `topic` and `subscription` you just `call once ServiceBusQueueOutput` for `3 subscriptions(same queue)`


```java
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

public class OrderReceiverFunctionTopic {

    @FunctionName("OrderReceiverTopic")
    public void run(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS
        ) String orderJson,

        @ServiceBusTopicOutput(
            name = "topicOut",
            topicName = "%TOPIC_NAME%",
            connection = "serviceBus_connectionString"
        ) OutputBinding<String> topicOut,

        final ExecutionContext context
    ) {
        context.getLogger().info("Received new order: " + orderJson);
        topicOut.setValue(orderJson);
    }
}
```

```java
public class BillingProcessorTopic {
    @FunctionName("BillingProcessorTopic")
    public void run(
        @ServiceBusTopicTrigger(
            name = "message",
            topicName = "%TOPIC_NAME%",
            subscriptionName = "billing-sub",
            connection = "serviceBus_connectionString"
        ) String message,
        final ExecutionContext context
    ) {
        context.getLogger().info("Billing Service received: " + message);
    }
}
```
```java
public class ShippingProcessorTopic {
    @FunctionName("ShippingProcessorTopic")
    public void run(
        @ServiceBusTopicTrigger(
            name = "message",
            topicName = "%TOPIC_NAME%",
            subscriptionName = "shipping-sub",
            connection = "serviceBus_connectionString"
        ) String message,
        final ExecutionContext context
    ) {
        context.getLogger().info("Shipping Service received: " + message);
    }
}
```
```java
public class EmailProcessorTopic {
    @FunctionName("EmailProcessorTopic")
    public void run(
        @ServiceBusTopicTrigger(
            name = "message",
            topicName = "%TOPIC_NAME%",
            subscriptionName = "email-sub",
            connection = "serviceBus_connectionString"
        ) String message,
        final ExecutionContext context
    ) {
        context.getLogger().info("Email Service received: " + message);
    }
}
```

- local.settings.json
```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "orderServiceBus-connectString": "Endpoint=sb://<name-space>.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=privateKey"
    ,"TOPIC_NAME": "order-events"
  }
}
```

- On Azure, You need config `Environment Variables` same with queue Above.


----------------------
<br/>


## Blob Storage
- to handle special Data Type 
- Azure Blob Storage is Microsoft’s cloud service for storing unstructured data (files, images, videos, backups, logs, etc.) at massive scale.
- Files (images, videos, documents)
- Backups
- Logs
- Big binary datasets

- Organized:
```text
Storage Account
    └── Container  (like a folder)
           └── Blob (your actual file/data)
```
- `Storage Account` : top-level namespace in Azure.
- `Container` : a logical grouping of blobs.
- `Blob` :the actual object/file.

#
### Types Of Blobs
- Azure supports three main types:

| Blob Type       | Use Case                                                                 |
|-----------------|--------------------------------------------------------------------------|
| **Block blob**  | Most common; store files, text, binary data (can be uploaded in chunks). |
| **Append blob** | Optimized for adding data at the end (good for logs).                    |
| **Page blob**   | Optimized for random read/write (used for Azure VM disks).               |


#
### Prising
- Storage Capacity (GB/Month)
  - `Hot`: frequently accessed data, higher storage cost but low access cost.
  - `Cool`: infrequently accessed data, cheaper storage but higher access cost.
  - `Archive`: rarely accessed, cheapest storage but highest retrieval cost and latenc

**Rough example (US East region, typical monthly costs):**

| Tier    | Storage per GB | Read/Write per 10k ops | Data Retrieval per GB          | Notes                 |
|---------|----------------|------------------------|--------------------------------|-----------------------|
| Hot     | \~\$0.0184     | Low (a few cents)      | Included                       | For frequent access   |
| Cool    | \~\$0.01       | Higher than Hot        | \~\$0.01/GB                    | For infrequent access |
| Archive | \~\$0.00099    | Higher than Cool       | \~\$0.02/GB+ retrieval latency | For long-term storage |

### Example (Update later)

--------------------
<br/>

## @TimerTrigger (Scheduler Job)
- `@TimerTrigger` is an input binding annotation that lets your function run automatically on a schedule without needing any HTTP request, queue message, or event to trigger it.
- It’s basically a CRON job in the cloud — but managed by Azure, so you don’t need a separate scheduler service.

- **Example:**
- schedule = `{second} {minute} {hour} {day} {month} {day-of-week}`
```java
@FunctionName("MyTimerFunction")
public void run(
    @TimerTrigger(
        name = "timerInfo", 
        schedule = "0 */5 * * * *"  // Every 5 minutes
    ) String timerInfo,
    final ExecutionContext context) {

    context.getLogger().info("Timer function executed at: " + java.time.LocalDateTime.now());
}
```

- **Example 2:**
```java
@FunctionName("downloadFile")
public void run(
    @TimerTrigger(
        name = "download", 
        schedule = "%DOWNLOAD_INTERVAL%"  // Every 5 minutes
    ) String timerInfo,
    final ExecutionContext context) {

    context.getLogger().info("Timer function executed at: " + java.time.LocalDateTime.now());
}
```
- `local.settings.json` (config on local)
```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "UseDevelopmentStorage=true",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "DOWNLOAD_INTERVAL":"0 */5 * * * *"
  }
}
```
- if you use on Azure, you must config on `Environment Variables`
