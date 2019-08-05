package persist.postgres.repos

import persist.postgres.ActorPostgresDriver.api._
import persist.postgres.model.Country
import persist.postgres.ActorPostgresDriver
import slick.dbio.Effect
import slick.lifted.ProvenShape
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction}

final class CountriesRepo(tag: Tag) extends Table[Country](tag, "countries") {

  def countryName: Rep[String] = column[String]("country_name", O.PrimaryKey)

  def * : ProvenShape[Country] = countryName <> (Country.apply, Country.unapply)

}

object CountriesRepo {
  val countries: TableQuery[CountriesRepo] = TableQuery[CountriesRepo]

  def create(country: String): FixedSqlAction[Int, NoStream, Effect.Write] =
    countries += Country(country)

  def exists(country: String): FixedSqlAction[Boolean, ActorPostgresDriver.api.NoStream, Effect.Read] =
    countries.filter(_.countryName === country).exists.result

  def delete(country: String): FixedSqlAction[Int, NoStream, Effect.Write] =
    countries.filter(_.countryName === country).delete

  def getAll: FixedSqlStreamingAction[Seq[Country], Country, Effect.Read] = countries.result
}
