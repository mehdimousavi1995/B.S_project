package persist.cassandra.owner

import java.util.UUID

final case class Owner(
                        ownerId: UUID,
                        firstName: String,
                        lastName: String,
                        telegramUserId: Int,
                        createdAt: Long
                      )
