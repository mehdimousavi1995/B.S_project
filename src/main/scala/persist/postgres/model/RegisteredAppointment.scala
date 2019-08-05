package persist.postgres.model

import java.time.LocalDateTime

import util.TimeUtils

@SerialVersionUID(1L)
case class RegisteredAppointment (
                            id: Option[Int] = None,
                            chatId: String,
                            fullName: String,
                            flightDate: String,
                            fileId: String,
                            createdAt: LocalDateTime = TimeUtils.now,
                            deletedAt: Option[LocalDateTime] = None
                            )
