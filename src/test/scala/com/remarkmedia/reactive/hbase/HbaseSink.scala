package com.remarkmedia.reactive.hbase
import akka.actor.{ActorRef}
import akka.stream.scaladsl.Sink
/**
 * Created by hanrenwei on 10/23/15.
 */


case class HbaseSink[T](sink: Sink[T, Unit], underlyingCommitterActor: ActorRef)
