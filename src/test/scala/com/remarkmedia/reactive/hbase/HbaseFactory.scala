package com.remarkmedia.reactive.hbase
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{HBaseAdmin, HConnectionManager, HTableInterface}
/**
 * Created by hanrenwei on 10/22/15.
 */
class HbaseFactory(zookeepers: String) {
  val config = createConfiguration
  val connection = HConnectionManager.createConnection(config)
  val admin = new HBaseAdmin(config)

  def getHTable(table: Array[Byte]): HTableInterface = connection.getTable(table)

  private def createConfiguration(): Configuration = {
    val _config = HBaseConfiguration.create()
    _config.setStrings("hbase.zookeeper.quorum", zookeepers)
    _config.set("hbase.regionserver.lease.period", "86400000")
    _config
  }
}

object HbaseFactory{

}
