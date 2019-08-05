
import ai.bale.commons.kafka.KafkaExtension
import ai.bale.rabbit9._
import ai.bale.rabbit9.consistent.Rabbit9ConsistentManager
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import broker.rabbitmq.RabbitManagerExtension
import com.bazaar.messages.ExampleMessages
import com.rabbitmq.client.{AMQP, BuiltinExchangeType, Envelope}
import im.actor.serialization.ActorSerializer
import persist.postgres.PostgresDBExtension
import persist.postgres.model.Country
import persist.postgres.repos.CountriesRepo
import persist.redis.RedisExtension
import scalapb.GeneratedMessage
import sdk.CustomConfig

import scala.concurrent.Future
import scala.util.Random

object Main extends App {

  val config = CustomConfig.load()
  implicit val system: ActorSystem = ActorSystem("template", config)
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()
  val log = system.log
  ActorSerializer.register(85, classOf[ExampleMessages])

  //  val kafkaExt = KafkaExtension(system).broker
  val topic = "asghar"

  val msg = ExampleMessages("fuck you")
  //  Future {
  //    while (true) {
  //      kafkaExt.publish(topic, "fuck you", msg)
  //      Thread.sleep(1000)
  //    }
  //  }


  //  val flow: CommittableMessage[String, GeneratedMessage] => Future[Unit] = { producedMsg =>
  //    log.error("\n\n\n\n\n")
  //    log.error("produced message: {}", producedMsg)
  //    log.error("\n\n\n\n\n")
  //    Future.successful(Unit)
  //  }

  //
  //  val groupId = "group-id"
  //  Consumer.committableSource(
  //    kafkaExt.consumerSettings(groupId),
  //    Subscriptions.topics(topic)
  //  ).mapAsync(10)(flow).
  //    runForeach(res =>
  //      log.error("res: {}", res))

//
//  val rabbitExt = RabbitManagerExtension(system)
//  val routingKey = "asghar"
//  var count = 100
//  while (count > 0) {
//    rabbitExt.publish(routingKey, msg, "").map { result =>
//      log.error("result of publish to rabbitmq: {}", result)
//    }
//    count = count - 1
//  }
//
//
//  case class ConsumerClient(sendTo: ActorRef) extends Rabbit9ConsumerAdapter {
//
//    override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]): Unit = {
//      sendTo ! ActorSerializer.fromBinary(body)
//    }
//  }
//
//  case class MessageReceiver() extends Actor {
//    override def receive: Receive = {
//      case m =>
//        system.log.error("\n\n")
//        system.log.error("receive message: {}", m)
//        system.log.error("\n\n")
//
//    }
//  }
//
//  val actor = system.actorOf(Props(MessageReceiver()), "message-receiver")
//  val hashKeh = Random.nextInt()
//
//  private val queueArguments = new java.util.HashMap[String, AnyRef]()
//  queueArguments.put("x-queue-mode", "lazy")
//
//  val queueName = "queue.asghar"
//  val subscribeQueueInfo = SubscribeQueueInfo(queueName, durable = false, exclusive = true, autoDelete = true, queueArguments)
//  val subscribeConsumerInfo = SubscribeConsumerInfo(ConsumerClient(actor), Math.abs(Random.nextInt(10000)).toString, true)
//
//  rabbitExt.rabbitManager.subscribe(hashKeh, subscribeQueueInfo, subscribeConsumerInfo)
//  val subscribeBindingInfo = SubscribeBindingInfo(routingKey, Some(rabbitExt.botExchange))
//  rabbitExt.rabbitManager.bind(hashKeh, queueName, subscribeBindingInfo)



  val reidsExt = RedisExtension(system)

  reidsExt.set("asghar", "safdar")

  reidsExt.get("asghar").map { result =>
    log.error("get key from redis: {}", result)

  }


  val pdb = PostgresDBExtension(system).db
  pdb.run(CountriesRepo.create("USA"))
  pdb.run(CountriesRepo.getAll).map { result =>
    log.error("get data from postgres: {}", result)
  }


}
