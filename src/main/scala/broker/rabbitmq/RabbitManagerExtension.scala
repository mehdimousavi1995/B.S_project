package broker.rabbitmq

import akka.actor.ExtendedActorSystem

import ai.bale.rabbit9.consistent.Rabbit9ConsistentManager
import ai.bale.rabbit9.{ PublishDataInfo, RabbitResponse, SubscribeBindingInfo }
import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId }
import com.rabbitmq.client.BuiltinExchangeType
import com.typesafe.config.Config
import im.actor.serialization.ActorSerializer
import scalapb.GeneratedMessage

import scala.concurrent.Future
class RabbitManagerExtensionImpl(system: ExtendedActorSystem) extends Extension {
  val rabbitConf: Config = system.settings.config.getConfig("rabbit9")

  val rabbitManager: Rabbit9ConsistentManager = Rabbit9ConsistentManager(rabbitConf, msg ⇒ new String(msg).hashCode.toString)(system)

  val botExchange: String = rabbitConf.getString("topic-exchange-bot-updates")
  val groupExchange: String = rabbitConf.getString("topic-exchange-group-changes")
  val groupChanges: String = rabbitConf.getString("group-changes-topic-name")

  // TODO make it configurable (exchange-auto-create)
  rabbitManager.createExchange("", botExchange, BuiltinExchangeType.TOPIC, SubscribeBindingInfo("", None))
  rabbitManager.createExchange("", groupExchange, BuiltinExchangeType.TOPIC, SubscribeBindingInfo("", None))

  def publish(routineKey: String, msg: GeneratedMessage, hashKey: Any, exchangeName: String = botExchange): Future[RabbitResponse] = {
    val publishData = PublishDataInfo(ActorSerializer.toBinary(msg.asInstanceOf[AnyRef]), routineKey, Some(exchangeName))
    rabbitManager.publish(hashKey, publishData)
  }

}

object RabbitManagerExtension extends ExtensionId[RabbitManagerExtensionImpl] {
  override def createExtension(system: ExtendedActorSystem): RabbitManagerExtensionImpl = new RabbitManagerExtensionImpl(system)
}
