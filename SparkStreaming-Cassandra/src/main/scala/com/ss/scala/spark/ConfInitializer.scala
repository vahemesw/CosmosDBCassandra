package com.ss.scala.spark

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark._

object ConfInitializer {

  val config = ConfigFactory.load()

  /**
    * Get Spark conf with Cassandra properties
    * @return SparkConf
    */
  def getSparkCassandraConf() : SparkConf = {
    val conf = new SparkConf(true).set("spark.cassandra.connection.host", config.getString("spark.cassandra.connection.host"))
      .set("spark.cassandra.connection.port", config.getString("spark.cassandra.connection.port"))
      .set("spark.cassandra.auth.username", config.getString("spark.cassandra.auth.username"))
      .set("spark.cassandra.auth.password", config.getString("spark.cassandra.auth.password"))
      .set("spark.cassandra.connection.ssl.enabled", config.getString("spark.cassandra.connection.ssl.enabled"))
      .set("spark.cassandra.connection.ssl.trustStore.path", config.getString("spark.cassandra.connection.ssl.trustStore.path"))
      .set("spark.cassandra.connection.ssl.trustStore.password", config.getString("spark.cassandra.connection.ssl.trustStore.password"))
      .set("spark.cassandra.connection.ssl.enabledAlgorithms", config.getString("spark.cassandra.connection.ssl.enabledAlgorithms"))
      .setMaster("local[2]")
      .setAppName("cassandra-cosmosdb")

    conf
  }
}