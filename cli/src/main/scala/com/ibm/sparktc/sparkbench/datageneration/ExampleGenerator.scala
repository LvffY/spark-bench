package com.ibm.sparktc.sparkbench.datageneration

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SparkFuncs.writeToDisk
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
 * This is a case class that represents the columns for the one-row DataFrame that
 * we'll be returning at the end of generating the data.
 */
case class ExampleGeneratorResult(
                                   name: String,
                                   rows: Int,
                                   cols: Int,
                                   str: String,
                                   start_time: Long,
                                   create_time: Long,
                                   transform_time: Long,
                                   save_time: Long,
                                   total_runtime: Long
                                 )

/**
 * The ExampleDefaults object unzips the Map[String, Any] passed to it from the
 * config parsing structure. It validates the parameters, substitutes any defaults
 * as necessary, and then creates an instance of the ExampleGenerator case class.
 */
object ExampleDefaults extends WorkloadDefaults {
  val DEFAULT_STR = "foo"
  val name = "example-generator"

  override def apply(m: Map[String, Any]): ExampleGenerator =
    ExampleGenerator(
      numRows = getOrThrow(m, "rows").asInstanceOf[Int],
      numCols = getOrThrow(m, "cols").asInstanceOf[Int],
      output = Some(getOrThrow(m, "output").asInstanceOf[String]),
      str = m.getOrElse("str", DEFAULT_STR).asInstanceOf[String]
    )
}

/**
 * The ExampleGenerator case class has as constructor arguments every parameter
 * necessary to run the workload. Some arguments, like input and output, are
 * must be included according to the definition of Workload, which this case class is
 * extending. Because this is a data generator, it doesn't take in any data and we
 * can safely set the input parameter to be None by default.
 *
 * Classes that extend Workload must implement the method doWorkload() which
 * optionally takes in data (again, because this is a data generator we don't need to take
 * in any data), and returns a Dataframe NOT of the results of the workload itself,
 * but of the BENCHMARK results. The results of the workload itself are written out to
 * the location specified in the output parameter.
 */
case class ExampleGenerator(
                             numRows: Int,
                             numCols: Int,
                             input: Option[String] = None,
                             output: Option[String],
                             str: String,
                             saveMode: String = "overwrite"
                           ) extends Workload {

  private def createData(spark: SparkSession) = {
    // Create one row of our amazing data
    val oneRow = Seq.fill(numCols)(str).mkString(",")

    // Turn it into an RDD of size numRows x numCols
    val data: Seq[String] = for (i <- 0 until numRows) yield oneRow
    val strrdd: RDD[String] = spark.sparkContext.parallelize(data)
    strrdd.map(str => str.split(","))
  }

  private def createDataFrame(rdd: RDD[Array[String]], spark: SparkSession): DataFrame = {
    // In order to make a dataframe we'll need column names and a schema.
    // This just uses the column index as the name for each column.
    val schemaString = rdd.first().indices.map(_.toString).mkString(" ")
    val fields = schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, nullable = false))
    val schema = StructType(fields)
    val rowRDD: RDD[Row] = rdd.map(arr => Row(arr: _*))

    // Now we have our dataframe that we'll write to the location in output.
    spark.createDataFrame(rowRDD, schema)
  }

  /**
   * This is where we're doing the actual work of the workload
   */
  override def doWorkload(df: Option[DataFrame] = None, spark: SparkSession): DataFrame = {
    val startTime = System.currentTimeMillis
    // Generate the data and time all the different stages
    val (createTime, rdd) = time(createData(spark))
    val (transformTime, df) = time(createDataFrame(rdd, spark))
    val (saveTime, _) = time {
      writeToDisk(output.get, "overwrite", df, spark)
    }

    // And now let's use that case class from above to create the one-row dataframe of our benchmark results
    spark.createDataFrame(
      Seq(
        ExampleGeneratorResult(
          name = ExampleDefaults.name,
          rows = numRows,
          cols = numCols,
          str = str,
          start_time = startTime,
          create_time = createTime,
          transform_time = transformTime,
          save_time = saveTime,
          total_runtime = createTime + transformTime + saveTime
        ))
    )
  }
}