package util

import java.time.{ Instant, LocalDateTime, ZoneId, ZoneOffset }

object TimeUtils {
  val tehranZoneId = ZoneId.of("Asia/Tehran")
  val tehranZoneOffset = ZoneId.of("Asia/Tehran").getRules.getOffset(now)

  def ofEpochMilli(epoch: Long): LocalDateTime = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(epoch),
    ZoneOffset.UTC)

  def ofEpochMilliTehran(epoch: Long): LocalDateTime = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(epoch),
    tehranZoneId)

  def now: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
  def nowTehran: LocalDateTime = LocalDateTime.now(tehranZoneId)

  implicit class LocalDateTimeImplicits(date: LocalDateTime) {
    def toEpochMilli: Long = date.toInstant(ZoneOffset.UTC).toEpochMilli
    def toEpochMilliTehran: Long = date.toInstant(tehranZoneOffset).toEpochMilli
  }
}
