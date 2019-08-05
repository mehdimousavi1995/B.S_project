package persist.cassandra.device

import java.util.UUID

import com.outworkers.phantom.Table
import com.outworkers.phantom.builder.query.InsertQuery
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.keys.PartitionKey
import persist.cassandra.AppDatabaseProvider

import scala.concurrent.Future


abstract class DeviceRepo extends Table[DeviceRepo, Device] {

  object partitionKey extends StringColumn with PartitionKey

  object deviceId extends UUIDColumn with PrimaryKey

  object deviceName extends StringColumn

  object deviceType extends StringColumn

  object homeId extends UUIDColumn

  object createdAt extends LongColumn

  def store(partitionKey: String, device: Device): InsertQuery.Default[DeviceRepo, Device] =
    insert
      .value(_.partitionKey, partitionKey)
      .value(_.deviceId, device.deviceId)
      .value(_.deviceName, device.deviceName)
      .value(_.deviceType, device.deviceType)
      .value(_.homeId, device.homeId)
      .value(_.createdAt, device.createdAt)

  def findByPartition(partitionKey: String): Future[List[Device]] =
    select.where(_.partitionKey eqs partitionKey).fetch()

  def findById(partitionKey: String, deviceId: UUID): Future[Option[Device]] =
    select
      .where(_.partitionKey eqs partitionKey)
      .and(_.deviceId eqs deviceId)
      .one()

}


trait HomeService extends AppDatabaseProvider {

  def store(partitionKey: String, device: Device): Future[ResultSet] =
    db.devices.store(partitionKey, device).future()

  def findPartition(partitionKey: String): Future[List[Device]] =
    db.devices.findByPartition(partitionKey)

  def findById(partitionKey: String, deviceId: UUID): Future[Option[Device]] =
    db.devices.findById(partitionKey, deviceId)

  def batchStore(partitionKey: String, devices: Device*): Future[ResultSet] =
    Batch.logged.add(devices.map(home => db.devices.store(partitionKey, home))).future()

}