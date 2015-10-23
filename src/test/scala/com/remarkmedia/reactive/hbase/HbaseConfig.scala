package com.remarkmedia.reactive.hbase
import java.util.Properties
/**
 * Created by hanrenwei on 10/23/15.
 */
class HbaseConfig(properties:Properties) {

  def getTableName = properties.getProperty("table")

}
