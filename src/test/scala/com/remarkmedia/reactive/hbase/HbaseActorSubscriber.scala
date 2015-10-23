package com.remarkmedia.reactive.hbase

import akka.actor.ActorLogging
import akka.stream.actor.{ActorSubscriber, ActorSubscriberMessage, RequestStrategy}
import org.apache.hadoop.hbase.client.Result

/**
 * Created by hanrenwei on 10/23/15.
 */
private[kafka] class HbaseActorSubscriber(val producer: HbaseProducer,
                                             requestStrategyProvider: () => RequestStrategy
                                              )
  extends ActorSubscriber with ActorLogging {

  override protected val requestStrategy = requestStrategyProvider()

  def receive = {
    case ActorSubscriberMessage.OnNext(element) =>
      processElement(element.asInstanceOf[Results])
    case ActorSubscriberMessage.OnError(ex) =>
      handleError(ex)
    case ActorSubscriberMessage.OnComplete =>
      stop()
    case "close_producer" => producer.close()
  }

  private def processElement(element: Results) = {
    element
  }

  private def handleError(ex: Throwable) = {
    log.error(ex, "Stopping hbase subscriber due to fatal error.")
    stop()
  }

  def stop() = {
    cleanupResources()
    context.stop(self)
  }

  def cleanupResources(): Unit = producer.close()
}

case class Results(results: List[Result])