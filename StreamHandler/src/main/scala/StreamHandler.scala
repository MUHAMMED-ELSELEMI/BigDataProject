import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import org.apache.spark.sql.cassandra._

import com.datastax.oss.driver.api.core.uuid.Uuids // com.datastax.cassandra:cassandra-driver-core:4.0.0
import com.datastax.spark.connector._

case class ExpenseData(empno: Int, date_time: String, description: String, payment: Double, etype: String)

object StreamHandler {
  def main(args: Array[String]) {

    // Initialize Spark session
    val spark = SparkSession
      .builder
      .appName("Expense Stream Handler")
      .config("spark.cassandra.connection.host", "127.0.0.1:9042")
      .getOrCreate()

    import spark.implicits._

    // Read from Kafka
    val inputDF = spark
      .readStream
      .format("kafka") // kafka format
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "SAU") // Your Kafka topic name
      .load()

    // Select the 'value' column from Kafka, cast from bytes to string
    val rawDF = inputDF.selectExpr("CAST(value AS STRING)").as[String]

    // Split each row on comma and map to case class (including the 'type' field)
    val expandedDF = rawDF.map(row => row.split(","))
      .map(row => ExpenseData(
        row(0).toInt,         // empno
        row(1),               // date_time
        row(2),               // description
        row(3).toDouble,      // payment
        row(4)                // type (expense type)
      ))

    // Create a dataset function to generate UUIDs
    val makeUUID = udf(() => Uuids.timeBased().toString)

    // Add UUIDs to match Cassandra schema
    val expenseWithIDs = expandedDF.withColumn("uuid", makeUUID())

    // Write data to Cassandra table
    val query = expenseWithIDs
      .writeStream
      .trigger(Trigger.ProcessingTime("5 seconds")) // Time interval for processing
      .foreachBatch { (batchDF: DataFrame, batchID: Long) =>
        println(s"Writing batch $batchID to Cassandra")
        batchDF.write
          .cassandraFormat("expenses", "my_keyspace") // Table name, Keyspace name
          .mode("append")
          .save()
      }
      .outputMode("append")
      .start()

    // Await termination
    query.awaitTermination()
  }
}
