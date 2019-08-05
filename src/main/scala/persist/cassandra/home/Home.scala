package persist.cassandra.home

import java.util.UUID

final case class Home(
                       homeId: UUID,
                       address: String,
                       houseArea: Int,
                       ownerId: UUID,
                       createdAt: Long
                      )
