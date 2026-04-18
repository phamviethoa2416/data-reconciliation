package reader

import org.apache.spark.sql.DataFrame

trait JdbcMetadataReader {
  def listTables(): Seq[String]
  def getTableSchema(tableName: String): Map[String, String]
  def readTable(tableName: String): Option[DataFrame]
  def hasData(tableName: String): Boolean
}
