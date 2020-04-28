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

case class CacheTestResult(name: String, timestamp: Long, runTime1: Long, runTime2: Long, runTime3: Long, cacheTime: Long)

object CacheTest extends WorkloadDefaults {
  val name = "cachetest"

  def getCacheType(cacheString: String): StorageLevel = {
    cacheString match {
      case "DISK" => StorageLevel.DISK_ONLY
      case "RAM" => StorageLevel.MEMORY_ONLY
      case _ => StorageLevel.NONE
    }
  }

  def apply(m: Map[String, Any]): CacheTest =
    new CacheTest(input = m.get("input").map(_.asInstanceOf[String]),
      output = m.get("workloadresultsoutputdir").map(_.asInstanceOf[String]),
      sleepMs = getOrDefault[Long](m, "sleepMs", 1000L),
      cacheType = getCacheType(getOrDefault[String](m, "cacheType", "None")))
}

case class CacheTest(input: Option[String],
                     output: Option[String],
                     saveMode: String = SaveModes.error,
                     sleepMs: Long,
                     cacheType: StorageLevel) extends Workload {

  def doWorkload(df: Option[DataFrame], spark: SparkSession): DataFrame = {
    import spark.implicits._

    val read = df.getOrElse(Seq.empty[(Int)].toDF)

    val (resultTime1, _) = time(read.count)

    read.persist(cacheType)

    val (resultTime2, _) = time(read.count)


    val (resultTime3, _) = time(read.count)

    val now = System.currentTimeMillis()
    spark.createDataFrame(Seq(CacheTestResult("cachetest", now, resultTime1, resultTime2, resultTime3, Math.abs(resultTime2 - resultTime1))))
  }
}
