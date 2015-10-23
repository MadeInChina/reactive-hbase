package com.remarkmedia.reactive.hbase

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.actor.{ActorPublisher, ActorSubscriber, RequestStrategy, WatermarkRequestStrategy}
import com.remarkmedia.reactive.hbase.ReactiveHbase.DefaultRequestStrategy
import org.reactivestreams.Subscriber

/**
 * Created by hanrenwei on 10/23/15.
 */
class ReactiveHbase {

  def publish(
               props: ProducerProperties,
               requestStrategy: () => RequestStrategy
               )(implicit actorSystem: ActorSystem): Subscriber[Results] = {
    ActorSubscriber[Results](producerActor(props, requestStrategy))
  }


  def producerActor(
                     props: ProducerProperties,
                     requestStrategy: () => RequestStrategy
                     )(implicit actorSystem: ActorSystem): ActorRef = {
    producerActor(props, requestStrategy, "hbase-subscriber-dispatcher")
  }

  def producerActor(
                     props: ProducerProperties,
                     dispatcher: String
                     )(implicit actorSystem: ActorSystem): ActorRef = {
    producerActor(props, DefaultRequestStrategy, dispatcher)
  }

  def producerActor(
                     props: ProducerProperties,
                     requestStrategy: () => RequestStrategy,
                     dispatcher: String
                     )(implicit actorSystem: ActorSystem): ActorRef = {
    actorSystem.actorOf(producerActorProps(props, requestStrategy).withDispatcher(dispatcher))
  }

  def producerActorProps(
                          props: ProducerProperties,
                          requestStrategy: () => RequestStrategy
                          ) = {
    val producer = new HbaseProducer(props, null)
    Props(
      new HbaseActorSubscriber(producer, requestStrategy)
    )
  }

  def producerActorProps(props: ProducerProperties): Props = {
    producerActorProps(props, DefaultRequestStrategy)
  }

  def producerActor(
                     props: ProducerProperties
                     )(implicit actorSystem: ActorSystem): ActorRef = {
    actorSystem.actorOf(producerActorProps(props))
  }


  def consume(
               props: ConsumerProperties
               )(implicit actorSystem: ActorSystem) = {
    ActorPublisher[Results](consumerActor(props))
  }


  def consume(
               props: ConsumerProperties,
               dispatcher: String
               )(implicit actorSystem: ActorSystem) = {
    ActorPublisher[Results](consumerActor(props, dispatcher))
  }

  def consumerActor[T](props: ConsumerProperties)(implicit actorSystem: ActorSystem): ActorRef = {
    consumerActor(props, ReactiveHbase.ConsumerDefaultDispatcher)
  }

  def consumerActor(
                     props: ConsumerProperties,
                     dispatcher: String
                     )(implicit actorSystem: ActorSystem): ActorRef = {
    actorSystem.actorOf(consumerActorProps(props).withDispatcher(dispatcher))
  }

  private def consumerActorWithConsumer(
                                         props: ConsumerProperties,
                                         dispatcher: String
                                         )(implicit actorSystem: ActorSystem) = {
    val propsWithConsumer = consumerActorPropsWithConsumer(props)
    val actor = actorSystem.actorOf(propsWithConsumer.actorProps.withDispatcher(dispatcher))
    ConsumerWithActor(propsWithConsumer.consumer, actor)
  }

  private def consumerActorPropsWithConsumer(props: ConsumerProperties) = {
    val consumer = new HbaseConsumer(props)
    ConsumerWithActorProps(consumer, Props(new HbaseActorPublisher(consumer)))
  }

  def consumerActorProps(props: ConsumerProperties) = {
    val consumer = new HbaseConsumer(props)
    Props(new HbaseActorPublisher(consumer))
  }

}

object ReactiveHbase {
  val DefaultRequestStrategy = () => WatermarkRequestStrategy(10)
  val ConsumerDefaultDispatcher = "hbase-publisher-dispatcher"
}

private[hbase] case class ConsumerWithActorProps(consumer: HbaseConsumer, actorProps: Props)

private[hbase] case class ConsumerWithActor(consumer: HbaseConsumer, actor: ActorRef)