# Azure Function With Java
- [**All Annotation**](#all-annotation)
- [*@TableInput and @TableOutput*](#tableinput-and-tableoutput)
- [**@QueueTrigger And @QueueOutput**](#)
- [**@ServiceBusQueueTrigger vs @ServiceBusQueueOutput**](#servicebusqueuetrigger-vs-servicebusqueueoutput)
  - [*What is Service Bus?*](#what-is-service-bus-queue-in-azure)
  - [*Create On Azure*](#create-on-azure)
  - [*Setup On Local*](#setup-on-local)
  - [*Create Programmatically Via Java SDK*](#create-programmatically-via-java-sdk)


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
