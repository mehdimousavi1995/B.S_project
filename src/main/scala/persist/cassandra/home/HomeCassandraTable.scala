package persist.cassandra.home

import java.util.UUID

import com.outworkers.phantom.Table
import com.outworkers.phantom.builder.query.InsertQuery
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.keys.PartitionKey
import persist.cassandra.AppDatabaseProvider

import scala.concurrent.Future


abstract class HomeRepo extends Table[HomeRepo, Home] {

  object partitionKey extends StringColumn with PartitionKey

  object homeId extends UUIDColumn with PrimaryKey
  object address extends StringColumn
  object houseArea extends IntColumn
  object ownerId extends UUIDColumn
  object createdAt extends LongColumn

  def store(partitionKey: String, home: Home): InsertQuery.Default[HomeRepo, Home] =
    insert
      .value(_.partitionKey, partitionKey)
      .value(_.homeId, home.homeId)
      .value(_.address, home.address)
      .value(_.houseArea, home.houseArea)
      .value(_.ownerId, home)
      .value(_.createdAt, home.createdAt)

  def findByPartition(partitionKey: String): Future[List[Home]] =
    select.where(_.partitionKey eqs partitionKey).fetch()

  def findById(partitionKey: String, homeId: UUID): Future[Option[Home]] =
    select
      .where(_.partitionKey eqs partitionKey)
      .and(_.homeId eqs homeId)
      .one()

}


trait HomeService extends AppDatabaseProvider {

  def store(partitionKey: String, home: Home): Future[ResultSet] =
    db.homes.store(partitionKey, home).future()

  def findPartition(partitionKey: String): Future[List[Home]] =
    db.homes.findByPartition(partitionKey)

  def findById(partitionKey: String, homeId: UUID): Future[Option[Home]] =
    db.homes.findById(partitionKey, homeId)

  def batchStore(partitionKey: String, homes: Home*): Future[ResultSet] =
    Batch.logged.add(homes.map(home => db.homes.store(partitionKey, home))).future()

}