package com.remarkmedia.reactive.hbase

import akka.actor.Actor.Receive
import akka.stream.actor.ActorPublisher

/**
 * Created by hanrenwei on 10/23/15.
 */
private[hbase] class HbaseActorPublisher(consumer:HbaseConsumer) extends  ActorPublisher{
  override def receive: Receive = ???
}
