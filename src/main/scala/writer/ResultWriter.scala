package writer

import config.AppConfig
import model.TableReconResult
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

class ResultWriter(runId: String)(implicit spark: SparkSession) {
  private val resultSchema = StructType(Seq(
    StructField("run_id",       StringType),
    StructField("run_time",     StringType),
    StructField("db_name",      StringType),
    StructField("table_name",   StringType),
    StructField("check_type",   StringType),
    StructField("status",       StringType),
    StructField("source_value", StringType),
    StructField("sink_value",   StringType),
    StructField("detail",       StringType),
    StructField("duration_ms",  LongType)
  ))

  def write(results: Seq[TableReconResult]): Unit = {
    if (results.isEmpty) return
    val rows = results.map(r => Row(
      r.runId, r.runTime, r.dbName, r.tableName,
      r.checkType, r.status, r.sourceValue, r.sinkValue,
      r.detail, r.durationMs
    ))

    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(rows),
      resultSchema
    )

    df.write
      .mode("append")
      .partitionBy("db_name", "run_time")
      .parquet(AppConfig.RESULT_HDFS_PATH)
  }
}
