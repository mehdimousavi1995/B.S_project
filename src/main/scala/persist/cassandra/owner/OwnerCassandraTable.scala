package persist.cassandra.owner
import java.util.UUID
import com.outworkers.phantom.Table
import com.outworkers.phantom.builder.query.InsertQuery
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.keys.PartitionKey
import persist.cassandra.AppDatabaseProvider
import scala.concurrent.Future


abstract class OwnerRepo extends Table[OwnerRepo, Owner] {

  object partitionKey extends StringColumn with PartitionKey
  object ownerId extends UUIDColumn with PrimaryKey
  object firstName extends StringColumn
  object lastName extends StringColumn
  object telegramUserId extends IntColumn
  object createdAt extends LongColumn

  def store(partitionKey: String, owner: Owner): InsertQuery.Default[OwnerRepo, Owner] =
    insert
      .value(_.partitionKey, partitionKey)
      .value(_.ownerId, owner.ownerId)
      .value(_.firstName, owner.firstName)
      .value(_.lastName, owner.lastName)
      .value(_.telegramUserId, owner.telegramUserId)
      .value(_.createdAt, owner.createdAt)

  def findByPartition(partitionKey: String): Future[List[Owner]] =
    select.where(_.partitionKey eqs partitionKey).fetch()

  def findById(partitionKey: String, ownerId: UUID): Future[Option[Owner]] =
    select
      .where(_.partitionKey eqs partitionKey)
      .and(_.ownerId eqs ownerId)
      .one()

}


trait OwnerService extends AppDatabaseProvider {

  def store(partitionKey: String, owner: Owner): Future[ResultSet] =
    db.owners.store(partitionKey, owner).future()

  def findPartition(partitionKey: String): Future[List[Owner]] =
    db.owners.findByPartition(partitionKey)

  def findById(partitionKey: String, ownerId: UUID): Future[Option[Owner]] =
    db.owners.findById(partitionKey, ownerId)

  def batchStore(partitionKey: String, owners: Owner*): Future[ResultSet] =
    Batch.logged.add(owners.map(owner => db.owners.store(partitionKey, owner))).future()

}