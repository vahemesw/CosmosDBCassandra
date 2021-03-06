package com.ss.scala.spark
import java.nio.charset.Charset
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import com.datastax.spark.connector._
import com.datastax.spark.connector.writer._
import com.datastax.spark.connector.streaming._

object CassandraDataTypesSample extends App {

    val host = "localhost:2181" // zookeeper host and port

    val conf = ConfInitializer.getSparkCassandraConf()
    val ssc = new StreamingContext(conf, Seconds(5))

    val topic = "all-data-types" // the topic to listen to

    println("CREATING STREAMING CONTEXT")
    val lines = KafkaUtils.createStream(ssc, host, topic, Map(topic -> 1)).map(_._2)
    lines.print(5)


    // To insert and update data TODO - add inet, blob,
    val data = lines.map(l => l.split(",")).map(a => (a(0).toString, a(1).toString, a(2).toFloat,
        a(3).toDouble, a(4).toBoolean, a(5).toInt)).saveToCassandra("demo", "itemdata")


    // To select data based on primary key
    println("Get items with brand name as 'ABC'")
    val rdd = ssc.cassandraTable("demo", "itemdata").select("item_name", "available_units").where("item_brand_name = 'ABC'")
        .collect().foreach(println)



    // To select data based on non-primary key
    println("Get items with brand name as 'ABC' and units available > 100")
    val availability_rdd = ssc.cassandraTable("demo", "itemdata").select("item_name", "available_units")
        .where("item_brand_name = 'ABC' and available_units > 100").collect().foreach(println)


    // LIMIT the select results
    val deficit_rdd = ssc.cassandraTable("demo", "itemdata").select("item_name", "available_units")
            .where("item_brand_name = 'ABC' and available_units <= 80").limit(2).collect().foreach(println)


    // delete all columns based on condition
    ssc.cassandraTable("demo", "itemdata").where("item_brand_name = 'ABC' and available_units > 100").deleteFromCassandra("demo", "itemdata")


    // delete all data in one partition
    ssc.cassandraTable("demo", "itemdata").where("item_brand_name = 'XYZ'").deleteFromCassandra("demo", "itemdata")


    // To delete some columns
    ssc.cassandraTable("demo", "itemdata").where("item_brand_name = 'MNO' and expired = true")
    .deleteFromCassandra("demo", "itemdata", SomeColumns("available_units"))

    ssc.start()
    ssc.awaitTermination()
}
