package persist.postgres

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import akka.event.Logging
import com.typesafe.config.{Config, ConfigFactory}
import configs.syntax._
import org.flywaydb.core.Flyway
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.hikaricp.HikariCPJdbcDataSource
import slick.jdbc.{DataSourceJdbcDataSource, JdbcDataSource}

import scala.util.{Failure, Success, Try}

trait PostgresDBExtension extends Extension {
  val db: JdbcDataSource
}

final class PostgresDBExtensionImpl(val db: Database, conf: Config) extends Extension with FlywayInit {

  lazy val flyway: Flyway = {
    val ds = db.source match {
      case s: HikariCPJdbcDataSource   ⇒ s.ds
      case s: DataSourceJdbcDataSource ⇒ s.ds
      case s                           ⇒ throw new IllegalArgumentException(s"Unknown DataSource: ${s.getClass.getName}")
    }

    initFlyway(ds, "migration")
  }

  def clean(): Unit = flyway.clean()

  def migrate(): Unit = flyway.migrate()

}

object PostgresDBExtension extends ExtensionId[PostgresDBExtensionImpl] with ExtensionIdProvider {
  private val JndiPath = "DefaultBotDatabase"

  private var _system: ActorSystem = _

  def system: ActorSystem = _system

  override def lookup: PostgresDBExtension.type = PostgresDBExtension

  override def createExtension(system: ExtendedActorSystem): PostgresDBExtensionImpl = {
    this._system = system
    val log = Logging(system, getClass)
    val botDataBase = initDB(ConfigFactory.load())
    val db = new PostgresDBExtensionImpl(botDataBase, system.settings.config)
    system.registerOnTermination {
      db.db.close()
    }

    Try(db.migrate()) match {
      case Success(_) ⇒
        log.info("\n### Migration was successful ###\n")
      case Failure(e) ⇒
        log.error(e, "\n--- Migration failed ---\n")
        throw e
    }

    db
  }

  private def initDB(config: Config): Database = {
    val sqlConfig = config.getConfig("services.postgresql.db")

    val useConfig = for {
      host ← sqlConfig.get[Try[String]]("host").value
      port ← sqlConfig.get[Try[Int]]("port").value
      db ← sqlConfig.get[Try[String]]("db").value
      _ ← sqlConfig.get[Try[String]]("user").value
      _ ← sqlConfig.get[Try[String]]("password").value
    } yield sqlConfig.withFallback(ConfigFactory.parseString(
      s"""
         |url: "jdbc:postgresql://"$host":"$port"/"$db
      """.stripMargin
    ))

    val db = Database.forConfig("", useConfig.get)
    JNDI.initialContext.rebind(JndiPath, db)
    db
  }
}