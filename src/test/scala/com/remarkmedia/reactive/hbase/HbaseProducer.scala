package com.remarkmedia.reactive.hbase

import com.remarkmedia.reactive.hbase.BytesConversions._
import org.apache.hadoop.hbase.client.Scan

/**
 * Created by hanrenwei on 10/23/15.
 */
case class HbaseProducer(props: ProducerProperties, scan: Scan) {
  val producer = new HbaseFactory(props.zookeepers).getHTable(props.toProducerConfig.getTableName).getScanner(scan)

  def next(int: Int) = {
    producer.next(int)
  }

  def close() ={
    producer.close()
  }
}
