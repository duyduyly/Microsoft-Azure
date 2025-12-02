# Use Azure CLI

- [**Login to your Azure account**](#Login-to-your-azure-account)
    - [Login](#Login)
    - [Set Subscription after Login](#Set-Subscription-after-Login)
- [**Resource Group**](#Resource-Group)
    - [Create Resource Group](#Create-Resource-Group)
    - [List Resource Groups](#List-Resource-Groups)
    - [Delete Resource Group](#Delete-Resource-Group)
- [**Storage Account**](#Storage-Account)
    - [Create Storage Account](#Create-Storage-Account)
    - [List Storage Accounts](#List-Storage-Accounts)
    - [Delete Storage Account](#Delete-Storage-Account)
    - [Get Storage Account Connection String](#Get-Storage-Account-Connection-String)
    - [Show Storage Account Keys](#Show-Storage-Account-Keys)
- [**Blob Storage**](#Blob-Storage)
    - [List Blob Containers](#List-Blob-Containers)
    - [create Blob Container](#create-Blob-Container)
    - [Upload Blob to Container](#Upload-Blob-to-Container)
    - [Download Blob from Container](#Download-Blob-from-Container)
    - [Delete Blob from Container](#Delete-Blob-from-Container)
    - [List Blobs in Container](#List-Blobs-in-Container)

## Login to your Azure account

### Login

```bash
az login
```

### Set Subscription after Login:

```bash
No     Subscription name           Subscription ID                       Tenant
-----  --------------------------  ------------------------------------  --------
[1] *  demo-subscription-1         uuid1                                  alan
[2]    demo-subscription-2         uuid2                                  alan

The default is marked with an *; the default tenant is 'alan' and subscription is 'demo-subscription-1' (uuid1).

Select a subscription and tenant (Type a number or Enter for no changes): [1 or 2]
```

### Or set subscription by id or name

```bash
az account set --subscription "demo-subscription-1"
```

---------------------
<br/>

## Resource Group

### Create Resource Group

```bash
az group create --name myResourceGroup --location southeastasia
```

### List Resource Groups

```bash
az group list -o table
```

### Delete Resource Group

```bash
az group delete --name myResourceGroup
Are you sure you want to perform this operation? (y/n): y or n 
```

---------------------
<br/>

## Storage Account

### Create Storage Account

```bash
az storage account create --name mystorageaccount  --resource-group myResourceGroup --location southeastasia --sku Standard_LRS --kind StorageV2
```

- Common SKU options:
    - Standard_LRS (most common)
    - Standard_GRS
    - Standard_ZRS
    - Premium_LRS

#

### List Storage Accounts

```bash
az storage account list -o table
```

#

### Delete Storage Account

```bash
az storage account delete --name mystorageaccount --resource-group myResourceGroup
Are you sure you want to perform this operation? (y/n): y or n
```

#

### Get Storage Account Connection String

```bash
az storage account show-connection-string --name mystorage12345 --resource-group my-rg
```

#

### Show Storage Account Keys

```bash
az storage account keys list --resource-group <resource-name> --account-name <storage-account-name>
```

-----------------
<br/>

#

### Blob Storage

#### List Blob Containers

```bash
az storage container list --account-name mystorage1234 -o table
```

#

#### create Blob Container

```bash
az storage container create --name <container-name> --account-name <account-name>
```

#

#### Upload Blob to Container

```bash
az storage blob upload --container-name <container-name> --file C:\Demo-file.txt --name demo-file.txt --account-name <account-name>
```

#

#### Download Blob from Container

```bash
az storage blob download --container-name <container-name> --name data.pdf --file local.pdf --account-name <account-name>
```

#

#### Delete Blob from Container

```bash
az storage blob delete --container-name <container-name> --name data.pdf --account-name <account-name>
```

#

#### List Blobs in Container

```bash
az storage blob list --container-name <container-name> --account-name <account-name> -o table
```

-----------------
<br/>

## Service bus

### 1.Create Service Bus Namespace + Queue
### Create Service Bus Namespace + Queue
```bash
# Create namespace
az servicebus namespace create --resource-group <resource_name> --name <namespace> --location southeastasia --sku Standard

# Create queue
az servicebus queue create --resource-group <resource_name> --namespace-name <namespace> --name <queue_name>
```

### List Service Bus Namespace
```bash
az servicebus namespace list -o table
```
### Delete Service Bus Namespace
```bash
az servicebus namespace delete --resource-group <resource_name> --name <namespace>
``` 



#
### 2. Manage Authorization Rule

| Policy                        | Rule                   | Case                                                  |
|-------------------------------|------------------------|-------------------------------------------------------|
| **RootManageSharedAccessKey** | Manage + Send + Listen | use for admin, Automation script, CI/CD, Migration    |
| **SendOnlyPolicy**            | Send                   | App send message into all queues/topics in namespace  |
| **ListenOnlyPolicy**          | Listen                 | Worker read message from any queue/topic in namespace |

#### Watch list authorization rule

```bash
 az servicebus namespace authorization-rule keys list --resource-group <resource-name> --namespace-name <namespace> --name RootManageSharedAccessKey
```

#### Create authorization rule

```bash
az servicebus namespace authorization-rule create --resource-group <resource_name> --namespace-name <name_space> --name SendOnly --rights Send
````

#### Rollback authorization rule
```bash
az servicebus namespace authorization-rule keys renew --resource-group my-rg --namespace-name my-sb-namespace --name SendOnly --key PrimaryKey
````

#
### 3. Show namespace
#### show namespace

```bash

#show list namespace
az servicebus namespace list -o table

# show details namespace
az servicebus namespace show --resource-group rg-demo --name my-sb-namespace
```

#
### 4. Queue operations
#### create queue
```bash
az servicebus queue create --resource-group <resource_name> --namespace-name <namespace> --name <queue_name>
```

#
#### Watch queue
```bash
# List queues
az servicebus queue list --resource-group <resource_name> --namespace-name <namespace> -o table

# Show queue details
az servicebus queue show --resource-group <resource_name> --namespace-name <namespace> --name <queue_name>
```

#
#### Delete queue
```bash
az servicebus queue delete --resource-group <resource_name> --namespace-name <namespace> --name <queue_name>
```

### 5. Topic operations
#### create topic
```bash
az servicebus topic create --resource-group <resource_name> --namespace-name <namespace> --name <topic_name>
```

#
#### create subscription for topic
```bash
az servicebus topic subscription create --resource-group <resource_name> --namespace-name <namespace> --topic-name <topic_name> --name <subscription_name>
```

#
#### Watch topic
```bash
# List topics
az servicebus topic list --resource-group <resource_name> --namespace-name <namespace> -o table

# Show topic details
az servicebus topic show --resource-group <resource_name> --namespace-name <namespace> --name <topic_name>
```

#### Delete topic
```bash
az servicebus topic delete --resource-group <resource_name> --namespace-name <namespace> --name <topic_name>
```

#### Delete subscription
```bash
az servicebus topic subscription delete --resource-group <resource_name> --namespace-name <namespace> --topic-name <topic_name> --name <subscription_name>
``` 

#### Filter Rules for subscription
```bash
# Create filter rule
az servicebus topic subscription rule create --resource-group <resource_name> --namespace-name <namespace> --topic-name <topic_name> --subscription-name <subscription_name> --name <rule_name> --filter-sql-expression "<sql_expression>"
```

#
### 6. Send and Receive Messages
#### Send message to queue
```bash
az servicebus message send --resource-group <resource_name> --namespace-name <namespace> --queue-name <queue_name> --body "Hello, World!"
```

#### Receive message from queue
```bash
az servicebus message receive --resource-group <resource_name> --namespace-name <namespace> --queue-name <queue_name> --max-message-count 1 --peek-lock
``` 
#### Send message to topic
```bash
az servicebus message send --resource-group <resource_name> --namespace-name <namespace> --topic-name <topic_name> --body "Hello, World!"
``` 
#### Receive message from subscription
```bash
az servicebus message receive --resource-group <resource_name> --namespace-name <namespace> --topic-name <topic_name> --subscription-name <subscription_name> --max-message-count 1 --  peek-lock
```

### 7.Get key connection string

```bash
az servicebus queue create --resource-group <resource_name> --namespace-name <namespace> --name orders-queue
```