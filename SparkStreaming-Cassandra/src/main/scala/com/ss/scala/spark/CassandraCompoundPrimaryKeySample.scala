package com.ss.scala.spark

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import com.datastax.spark.connector._
import com.datastax.spark.connector.writer._
import com.datastax.spark.connector.streaming._

object CassandraCompoundPrimaryKeySample extends App {

    val host = "localhost:2181" // zookeeper host and port

    val conf = ConfInitializer.getSparkCassandraConf()
    val ssc = new StreamingContext(conf, Seconds(5))

    val topic = "all-data-types" // the topic to listen to

    println("CREATING STREAMING CONTEXT")
    val lines = KafkaUtils.createStream(ssc, host, topic, Map(topic -> 1)).map(_._2)
    lines.print(5)

    // Insert data
    val distinct_data = lines.map(l => l.split(",")).map(a => (a(0).toString, a(1).toString, a(5).toInt, a(3).toDouble, a(4).toBoolean, a(2).toFloat))
        .saveToCassandra("demo", "compound_primarykey_test", writeConf = WriteConf(ifNotExists = true))

    // To select data based on primary key
    println("Get items with select on partition keys")
    val rdd = ssc.cassandraTable("demo", "compound_primarykey_test")
        .select("item_name","available_units")
        .where("item_brand_name = 'ABC' and item_name = 'cycles'").collect().foreach(println)


    // To select data based on non-primary key
    println("Get items with brand name as 'ABC' and units available > 20")
    val availability_rdd = ssc.cassandraTable("demo", "compound_primarykey_test").select("item_name", "available_units")
        .where("item_brand_name = 'ABC' and item_name = 'chairs'").where("available_units >= 20")
        .limit(3).collect().foreach(println)


    // query based on cluster key TODO
    val abundant_items_rdd = ssc.cassandraTable("demo", "compound_primarykey_test")
        .select("expired", "available_units")
        .where("item_brand_name = 'MNO' and item_name = 'cots' and rack_number = 3")
        .collect().foreach(println)


    // delete rows based on condition
    ssc.cassandraTable("demo", "compound_primarykey_test")
        .where("item_brand_name = 'ABC' and item_name = 'cots' and available_units > 100")
        .deleteFromCassandra("demo", "compound_primarykey_test")


    // delete all data in one partition
    ssc.cassandraTable("demo", "compound_primarykey_test").where("item_brand_name = 'XYZ' and item_name = 'cots'")
        .deleteFromCassandra("demo", "compound_primarykey_test")


    // To delete some columns
    ssc.cassandraTable("demo", "compound_primarykey_test")
        .where("item_brand_name = 'MNO' and item_name = 'toys'")
        .deleteFromCassandra("demo", "compound_primarykey_test", SomeColumns("available_units"))

    ssc.start()
    ssc.awaitTermination()
}
