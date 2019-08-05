
import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

import com.datastax.driver.core.SocketOptions
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import com.outworkers.phantom.dsl._
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, WordSpecLike}
import persist.cassandra.{AppDatabase, AppDatabaseProvider}
import persist.cassandra.user.{Message, MessageService}

import scala.language.reflectiveCalls

class MessageServiceSpec
  extends WordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ScalaFutures
    with OptionValues {


  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(200, Millis))

  object TestConnector {
    private val config: Config = ConfigFactory.parseString("""cassandra {
                                                             |  host: "127.0.0.1"
                                                             |  port: 9042
                                                             |  keyspace: "scala_cassandra_example"
                                                             |}
                                                           """.stripMargin)

    val connection: CassandraConnection =
      ContactPoint(config.getString("cassandra.host"), config.getInt("cassandra.port"))
        .withClusterBuilder(
          _.withSocketOptions(
            new SocketOptions()
              .setConnectTimeoutMillis(20000)
              .setReadTimeoutMillis(20000)
          ))
        .noHeartbeat()
        .keySpace(
          KeySpace(config.getString("cassandra.keyspace"))
            .ifNotExists()
            .`with`(
              replication eqs SimpleStrategy.replication_factor(1)
            )
        )
  }

  object TestDatabase extends AppDatabase(TestConnector.connection)

  trait TestDatabaseProvider extends AppDatabaseProvider {
    override def database: AppDatabase = TestDatabase
  }

  val messageService = new MessageService with TestDatabaseProvider

  override def beforeAll(): Unit = {
    messageService.database.create()
    ()
  }

  override def afterAll(): Unit = {
//    messageService.database.drop()
//    ()
  }

  "message service" should {

    "store and find by id" in {
      val partition: (String, String) = ("A", "1")
      val message = Message(UUID.randomUUID(),
        "Test",
        LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond)
      val q = for {
        _ <- messageService.store(partition, message)
        find <- messageService.findById(partition, message.id)
      } yield find

      whenReady(q) { find =>
        find shouldBe defined
        find.value shouldBe message
      }
    }

    "batch store and find by partition" in {
      val partition: (String, String) = ("B", "1")
      val messages = Seq
        .range(0, 10)
        .map(
          i =>
            Message(UUID.randomUUID(),
              "Test" + i,
              LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond))

      val q = for {
        _ <- messageService.batchStore(partition, messages: _*)
        res <- messageService.findPartition(partition)
      } yield res

      whenReady(q) { res =>
        res.size shouldBe 10
      }
    }
  }
}