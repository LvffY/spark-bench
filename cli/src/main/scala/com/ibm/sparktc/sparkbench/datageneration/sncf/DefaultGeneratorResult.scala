package com.ibm.sparktc.sparkbench.datageneration.sncf

/**
 * This is a case class that represents the columns for the one-row DataFrame that
 * we'll be returning at the end of generating the data.
 */
case class DefaultGeneratorResult(
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
