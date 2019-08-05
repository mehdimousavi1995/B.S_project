package persist.cassandra

import com.outworkers.phantom.database.{Database, DatabaseProvider}
import com.outworkers.phantom.dsl.CassandraConnection
import persist.cassandra.device.DeviceRepo
import persist.cassandra.home.HomeRepo
import persist.cassandra.owner.OwnerRepo

class AppDatabase(override val connector: CassandraConnection)
  extends Database[AppDatabase](connector) {
  object owners extends OwnerRepo with Connector
  object homes extends HomeRepo with Connector
  object devices extends DeviceRepo with Connector
}

trait AppDatabaseProvider extends DatabaseProvider[AppDatabase]
