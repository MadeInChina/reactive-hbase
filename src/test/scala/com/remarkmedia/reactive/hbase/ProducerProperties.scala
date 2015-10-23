package com.remarkmedia.reactive.hbase

/**
 * Created by hanrenwei on 10/23/15.
 */

import java.util.Properties

object ProducerProperties {

  /**
   * Producer Properties
   *
   * brokerList
   * This is for bootstrapping and the producer will only use it for getting metadata (topics, partitions and replicas).
   * The socket connections for sending the actual data will be established based on the broker information returned in
   * the metadata. The format is host1:port1,host2:port2, and the list can be a subset of brokers or a VIP pointing to a
   * subset of brokers.
   *
   * topic
   * The high-level API hides the details of brokers from the consumer and allows consuming off the cluster of machines
   * without concern for the underlying topology. It also maintains the state of what has been consumed. The high-level API
   * also provides the ability to subscribe to topics that match a filter expression (i.e., either a whitelist or a blacklist
   * regular expression).  This topic is a whitelist only but can change with re-factoring below on the filterSpec
   *
   * clientId
   * The client id is a user-specified string sent in each request to help trace calls. It should logically identify
   * the application making the request.
   *
   */
  def apply[T](
                zookeepers: String,
                table: String
                ): ProducerProperties = {
    val props = initialMap(zookeepers, Some(table))
    new ProducerProperties(props, zookeepers)
  }

  def apply(brokerList: String, topic: String, clientId: String): ProducerProperties = {
    val props = initialMap(brokerList, Some(clientId))
    new ProducerProperties(props, topic)
  }

  def apply(brokerList: String, topic: String): ProducerProperties = {
    val props = initialMap(brokerList, None)
    new ProducerProperties(props, topic)
  }

  private def initialMap(brokerList: String, table: Option[String]) = {
    Map[String, String](
      "metadata.broker.list" -> brokerList,
      // defaults
      "table" -> table.getOrElse(""),
      "message.send.max.retries" -> "3",
      "request.required.acks" -> "-1",
      "producer.type" -> "sync"
    )
  }
}

case class ProducerProperties( private val params: Map[String, String],
                               zookeepers: String
                                  ) {

  /**
   * Asynchronous Mode
   * The number of messages to send in one batch when using async mode.
   * The producer will wait until either this number of messages are ready
   * to send or bufferMaxMs timeout is reached.
   */
  def asynchronous(batchSize: Int = 200, bufferMaxMs: Int = 500): ProducerProperties = {
    val p = params +(
      "producer.type" -> "async",
      "batch.num.messages" -> batchSize.toString,
      "queue.buffering.max.ms" -> bufferMaxMs.toString
      )
    copy(params = p)
  }


  /**
   * messageSendMaxRetries
   * This property will cause the producer to automatically retry a failed send request.
   * This property specifies the number of retries when such failures occur. Note that
   * setting a non-zero value here can lead to duplicates in the case of network errors
   * that cause a message to be sent but the acknowledgment to be lost.
   */
  def messageSendMaxRetries(num: Int): ProducerProperties = {
    copy(params = params + ("message.send.max.retries" -> num.toString))
  }

  /**
   * requestRequiredAcks
   * 0) which means that the producer never waits for an acknowledgment from the broker (the same behavior as 0.7).
   * This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
   * 1) which means that the producer gets an acknowledgment after the leader replica has received the data. This option provides
   * better durability as the client waits until the server acknowledges the request as successful (only messages that were
   * written to the now-dead leader but not yet replicated will be lost).
   * -1) which means that the producer gets an acknowledgment after all in-sync replicas have received the data. This option
   * provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
   */
  def requestRequiredAcks(value: Int): ProducerProperties = {
    copy(params = params + ("request.required.acks" -> value.toString))
  }

  /**
   * Set any additional properties as needed
   */
  def setProperty(key: String, value: String): ProducerProperties = copy(params = params + (key -> value))

  def setProperties(values: (String, String)*): ProducerProperties = copy(params = params ++ values)

  /**
   * Generate the Kafka ProducerConfig object
   *
   */
  def toProducerConfig: HbaseConfig = {
    new HbaseConfig(params.foldLeft(new Properties()) { (props, param) => props.put(param._1, param._2); props})
  }

  /**
   * Dump current props for debugging
   */
  def dump: String = params.map { e => f"${e._1}%-20s : ${e._2.toString}"}.mkString("\n")
}
