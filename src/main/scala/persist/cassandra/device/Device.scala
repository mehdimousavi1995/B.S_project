package persist.cassandra.device

import java.util.UUID

final case class Device(
                         deviceId: UUID,
                         deviceName: String,
                         deviceType: String,
                         homeId: UUID,
                         createdAt: Long
                      )
