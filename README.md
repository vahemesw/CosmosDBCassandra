# Spark streaming with Cosmos DB - Cassandra APIs


## Pre-requisites
* Kafka cluster
* Spark cluster - for the purpose of this demo we will use standalone spark installation on local dev machine
* sbt (preferably version 1.0.2)
* mvn
* Cosmos DB account

For the purpose of the demo, assume that all the installation is on local dev box.

The samples injest Kafka brokers data to Spark to be written into Cosmos DB using Cassandra APIs using Datastax spark streaming connector.

## Folder structure
* kafka-producer folder contains two Java classes that produce data and emit them to kafka brokers.
* SparkStreaming-Cassandra folder contains sample commands to save and query data from cosmos DB using Cassandra APIs

## Setup and building the code
1. Clone this repository using `git clone https://github.com/vahemesw/CosmosDBCassandra.git`

### Setting up kafka producer
1. Install and start kafka and zookeeper - see here for installation steps on windows - https://dzone.com/articles/running-apache-kafka-on-windows-os 

2. Create the folowing topics on your kafka cluster `pagevisit-data` and `all-data-types`
  Navigate to bin directory of your kafka installation and run the following commands
```
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic pagevisit-data
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic all-data-types
```

3. Kafka-producer has two main classes - KafkaDataProducer.java and kafkaProducerAllDataTypes.java
  Modify the `properties.put("metadata.broker.list", "localhost:9092")` with the list of kafka brokers adresses in case if you have a kafka cluster

4. Run the following maven command to produce the 'kafka-producer-0.0.1-SNAPSHOT-jar-with-dependencies.jar'
```
$\CosmosDB-Cassandra\kafka-producer>mvn clean compile package
```

5. Alternatively run the `KafkaDataProducer.java` program on local box to produce data for topic `pagevisit-data`.
 Likewise `kafkaProducerAllDataTypes.java` produces data for topic `all-data-types`

### Creating required tables in Cassandra

6. Execute commands in `SparkStreaming-Cassandra/src/main/resources/cql.script` to create Cassandra tables using cqlsh.

7. The commands create required tables to which data in the demo will be saved.

### Saving and querying data from Cosmos - DB using Cassandra APIs

8. Modify the values in SparkStreaming-Cassandra/src/main/resources/application.conf to your connection strings from portal.
```
spark{
	cassandra{
		connection{
			host = <cassandra host endpoint>
			port = <cassandra port>
			ssl{
				enabled = true
				enabledAlgorithms = TLS_RSA_WITH_AES_128_CBC_SHA
				trustStore{
					path = "<cacert path / self signed cert truststore path>"
					password = <your truststore password>
				}
			}
		}
		auth{
			username = <cosmos db account name>
			password = <cosmos db account password>
		}
	}
}
```
9. Modify the setMaster value to point to the Spark cluster in case you are not running spark on your dev-machine here - SparkStreaming-Cassandra/src/main/scala/com/ss/scala/spark/ConfInitializer.scala 
```
  val conf = new SparkConf().....setMaster("local[2]").setAppName("cassandra-cosmosdb")
```
10. Run the following command from root of SparkStreaming-Cassandra
```
CosmosDB-Cassandra\SparkStreaming-Cassandra>sbt run
```

11. You will be prompted to choose the scala application
```
Multiple main classes detected, select one to run:

 [1] com.ss.scala.spark.CassandraTestAllDataTypes
 [2] com.ss.scala.spark.CassandraTestWithCompoundPrimaryKey
 [3] com.ss.scala.spark.SimpleCassandraSaver

Enter number:
```

12. Select the class you chose to run, say 3

## Producing data and saving it to Cosmos DB

1. Run the `KafkaDataProducer.java`(you will need to run `kafkaProducerAllDataTypes.java` if you select `CassandraTestAllDataTypes` or `CassandraTestWithCompoundPrimaryKey` Scala applications to produce relevant data)

2. verify that the data is populated in the `demo.visitdetails`

3. Once the data is available in the table, run other queries (select, delete)


## More information

- [Azure Cosmos DB](https://docs.microsoft.com/azure/cosmos-db/introduction)

