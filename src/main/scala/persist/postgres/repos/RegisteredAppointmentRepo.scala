package persist.postgres.repos

import java.time.LocalDateTime

import persist.postgres.ActorPostgresDriver.api._
import persist.postgres.model.RegisteredAppointment
import util.TimeUtils
import slick.dbio.Effect
import slick.sql.{FixedSqlAction, SqlAction}


final class RegisteredAppointmentRepo(tag: Tag) extends Table[RegisteredAppointment](tag, "registered_appointment") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def chatId = column[String]("chat_id")

  def fullName = column[String]("full_name")

  def flightDate = column[String]("flight_date")

  def fileId = column[String]("file_id")

  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")

  def deletedAt: Rep[Option[LocalDateTime]] = column[Option[LocalDateTime]]("deleted_at")

  def * = (id.?, chatId, fullName, flightDate, fileId, createdAt, deletedAt) <> (RegisteredAppointment.tupled, RegisteredAppointment.unapply)

}

object RegisteredAppointmentRepo {

  val registeredAppointment = TableQuery[RegisteredAppointmentRepo]

  val activeRegisteredAppointment = registeredAppointment.filter(_.deletedAt.isEmpty)

  def create(r: RegisteredAppointment): FixedSqlAction[Int, NoStream, Effect.Write] =
    registeredAppointment += r

  def find(id: Int, chatId: String): SqlAction[Option[RegisteredAppointment], NoStream, Effect.Read] =
    activeRegisteredAppointment.filter(s => s.id === id && s.chatId === chatId).result.headOption

  def delete(id: Int, chatId: String): FixedSqlAction[Int, NoStream, Effect.Write] =
    activeRegisteredAppointment.filter(s => s.id === id && s.chatId === chatId).map(_.deletedAt).update(Some(TimeUtils.now))

}

