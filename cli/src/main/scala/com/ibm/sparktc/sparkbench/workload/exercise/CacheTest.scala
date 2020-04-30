/**
  * (C) Copyright IBM Corp. 2015 - 2017
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  */

package com.ibm.sparktc.sparkbench.workload.exercise

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions._
import com.ibm.sparktc.sparkbench.utils.SaveModes
import com.ibm.sparktc.sparkbench.workload.{Workload, WorkloadDefaults}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

case class CacheTestResult(name: String, timestamp: Long, runTime1: Long, runTime2: Long, runTime3: Long, cacheTime: Long, cacheType: String)

object CacheTest extends WorkloadDefaults {
  val name = "cachetest"

  def apply(m: Map[String, Any]): CacheTest =
    new CacheTest(input = m.get("input").map(_.asInstanceOf[String]),
      output = m.get("workloadresultsoutputdir").map(_.asInstanceOf[String]),
      sleepMs = getOrDefault[Long](m, "sleepMs", 1000L),
      cacheType = getOrDefault[String](m, "cache-type", "None"))
}

case class CacheTest(input: Option[String],
                     output: Option[String],
                     saveMode: String = SaveModes.error,
                     sleepMs: Long,
                     cacheType: String) extends Workload {

  private def dependingForCacheType[T](whenDiskOperation: T, whenRamOperation: T, otherCacheOperation: T): T =
    cacheType match {
      case "DISK" => whenDiskOperation
      case "RAM" => whenRamOperation
      case _ => otherCacheOperation
    }

  private def cacheDF(df: DataFrame): DataFrame =
    dependingForCacheType(df.persist(StorageLevel.DISK_ONLY), df.persist(StorageLevel.MEMORY_ONLY), df)

  private def cacheTime(t1: Long, t2: Long): Long = {
    val duration = Math.abs(t2 - t1)
    dependingForCacheType(duration, duration, 0)
  }

  def doWorkload(df: Option[DataFrame], spark: SparkSession): DataFrame = {

    val readDF = spark.read.csv(input.get)

    val (resultTime1, _) = time(readDF.count)

    cacheDF(readDF)

    val (resultTime2, _) = time(readDF.count)

    val (resultTime3, _) = time(readDF.count)

    val now = System.currentTimeMillis()
    spark.createDataFrame(Seq(CacheTestResult("cachetest", now, resultTime1, resultTime2, resultTime3, cacheTime(resultTime2, resultTime1), cacheType)))
  }
}
