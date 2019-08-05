package persist.postgres.model

import java.time.LocalDateTime

import util.TimeUtils



@SerialVersionUID(1L)
case class AdminCredential(chatId: String, fullName: String, createdAt: LocalDateTime = TimeUtils.now, deletedAt: Option[LocalDateTime] = None)