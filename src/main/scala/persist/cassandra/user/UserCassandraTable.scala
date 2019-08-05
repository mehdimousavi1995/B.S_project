package persist.cassandra.user

import java.util.UUID

import com.outworkers.phantom.Table
import com.outworkers.phantom.builder.query.InsertQuery
import com.outworkers.phantom.database.{Database, DatabaseProvider}
import com.outworkers.phantom.dsl.{CassandraConnection, _}
import com.outworkers.phantom.keys.PartitionKey

import scala.concurrent.Future

final case class Message(id: UUID, message: String, timestamp: Long)

abstract class Messages extends Table[Messages, Message] {

  object category extends StringColumn with PartitionKey
  object subcategory extends StringColumn with PartitionKey

  object id extends UUIDColumn with PrimaryKey
  object message extends StringColumn
  object timestamp extends LongColumn

  def store(partition: (String, String), msg: Message): InsertQuery.Default[Messages, Message] =
    insert
      .value(_.category, partition._1)
      .value(_.subcategory, partition._2)
      .value(_.id, msg.id)
      .value(_.message, msg.message)
      .value(_.timestamp, msg.timestamp)

  def findByPartition(partition: (String, String)): Future[List[Message]] =
    select.where(_.category eqs partition._1).and(_.subcategory eqs partition._2).fetch()

  def findById(partition: (String, String), id: UUID): Future[Option[Message]] =
    select
      .where(_.category eqs partition._1)
      .and(_.subcategory eqs partition._2)
      .and(_.id eqs id)
      .one()

}





class AppDatabase(override val connector: CassandraConnection)
  extends Database[AppDatabase](connector) {
  object messages extends Messages with Connector
}

trait AppDatabaseProvider extends DatabaseProvider[AppDatabase]

trait MessageService extends AppDatabaseProvider {

  def store(partition: (String, String), msg: Message): Future[ResultSet] =
    db.messages.store(partition, msg).future()

  def findPartition(partition: (String, String)): Future[List[Message]] =
    db.messages.findByPartition(partition)

  def findById(partition: (String, String), id: UUID): Future[Option[Message]] =
    db.messages.findById(partition, id)

  def batchStore(partition: (String, String), messages: Message*): Future[ResultSet] =
    Batch.logged.add(messages.map(msg => db.messages.store(partition, msg))).future()

}