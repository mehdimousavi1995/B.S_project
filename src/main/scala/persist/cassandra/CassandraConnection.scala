package persist.cassandra

import com.datastax.driver.core.SocketOptions
import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint}
import com.outworkers.phantom.dsl.{KeySpace, replication, _}
import com.typesafe.config.{Config, ConfigFactory}

import scala.language.reflectiveCalls
object CassandraConnection {
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
