package com.ibm.sparktc.sparkbench.datageneration.sncf

import com.ibm.sparktc.sparkbench.utils.GeneralFunctions.getOrThrow
import com.ibm.sparktc.sparkbench.workload.WorkloadDefaults

/**
 * The ExampleDefaults object unzips the Map[String, Any] passed to it from the
 * config parsing structure. It validates the parameters, substitutes any defaults
 * as necessary, and then creates an instance of the ExampleGenerator case class.
 */
object DefaultDataGenerator extends WorkloadDefaults {
  val DEFAULT_STR = "foo"
  val name = "default-generator"

  override def apply(m: Map[String, Any]): DefaultGenerator =
    DefaultGenerator(
      numRows = getOrThrow(m, "rows").asInstanceOf[Int],
      numCols = getOrThrow(m, "cols").asInstanceOf[Int],
      output = Some(getOrThrow(m, "output").asInstanceOf[String]),
      str = m.getOrElse("str", DEFAULT_STR).asInstanceOf[String]
    )
}
