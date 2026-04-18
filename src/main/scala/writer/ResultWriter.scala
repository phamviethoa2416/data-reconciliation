package writer

import config.AppConfig
import model.TableReconResult
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.col

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

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

  private val outputPath = s"${AppConfig.RESULT_HDFS_PATH}/$runId"

  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  private def now(): LocalDateTime =
    LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))

  def write(results: Seq[TableReconResult]): Unit = {
    if (results.isEmpty) return

    val currentTime = now()
    val runDate = currentTime.format(dateFormatter)

    val rows = results.map { r =>
      Row(
        r.runId,
        runDate,
        r.dbName,
        r.tableName,
        r.checkType,
        r.status,
        Option(r.sourceValue).getOrElse(""),
        Option(r.sinkValue).getOrElse(""),
        Option(r.detail).getOrElse(""),
        r.durationMs
      )
    }

    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(rows),
      resultSchema
    )

    val optimizedDF = df.repartition(col("db_name"))

    spark.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")

    optimizedDF.write
      .format("parquet")
      .mode(SaveMode.Overwrite)
      .partitionBy("run_time", "db_name")
      .option("compression", "snappy")
      .save(outputPath)
  }
}
