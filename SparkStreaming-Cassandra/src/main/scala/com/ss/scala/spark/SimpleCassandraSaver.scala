package com.ss.scala.spark

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import com.datastax.spark.connector._
import com.datastax.spark.connector.writer._
import com.datastax.spark.connector.streaming._

object SimpleCassandraSaver extends App {

    System.setProperty("spark.cassandra.connection.host", "13.92.96.123")
    System.setProperty("spark.cassandra.connection.port", "10350")
    System.setProperty("spark.cassandra.auth.username", "cassandratest")
    System.setProperty("spark.cassandra.auth.password",
        "bI51WUWssPUaVhP9pgpGVMJXlQhWKWz8NOZdfsGiCPGbSDQMxe1MWUF02f2yp3twtp5FLGntLOLZGJmggOe5MQ==")

    val conf = new SparkConf().setMaster("local[2]").setAppName("SimpleCassandraSaver")
    val ssc = new StreamingContext(conf, Seconds(5))

    val host = "localhost:2181"
    val topic = "pagevisit-docdb-data"
    println("CREATING STREAMING CONTEXT")
    val lines = KafkaUtils.createStream(ssc, host, topic, Map(topic -> 1)).map(_._2)
    lines.print(5)


    // To save data with columns specified
    val data = lines.map(l => l.split(",")).map(a => (a(0).toInt,a(1).toLong,  a(2).toString, a(3).toString, a(4).toString)).saveToCassandra("demo", "visitdetails", SomeColumns("event_id", "time", "site", "ip", "user_id"))


    // To select data based on primary key
    val rdd = ssc.cassandraTable("demo", "visitdetails").select("site", "ip").where("user_id = 'user1'").collect().foreach(println)


    /* requires ALLOW FILTERING and TOKEN support
    val usagerdd = ssc.cassandraTable("demo", "visitdetails").select("user_id", "ip").where("site= 'www.site3.com'").collect()
    usagerdd.take(5).foreach(println)
    */


    ssc.cassandraTable("demo", "visitdetails").where("user_id = 'user3'").deleteFromCassandra("demo", "visitdetails")

    ssc.start()
    ssc.awaitTermination()
}
