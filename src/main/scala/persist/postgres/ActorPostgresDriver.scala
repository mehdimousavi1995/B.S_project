package persist.postgres

import com.github.tminglei.slickpg._

//trait ByteStringImplicits {
//
//  implicit val byteStringColumnType = MappedColumnType.base[ByteString, Array[Byte]](
//    { bs ⇒ bs.toByteArray },
//    { ba ⇒ ByteString.copyFrom(ba) })
//}
//
//trait ProtoWrappersImplicits {
//
//  implicit val stringValueColumnType = MappedColumnType.base[StringValue, String](
//    { sv ⇒ sv.value },
//    { s ⇒ StringValue(s) })
//
//  implicit val int32ValueColumnType = MappedColumnType.base[Int32Value, Int](
//    { iv ⇒ iv.value },
//    { i ⇒ Int32Value(i) })
//}

trait ActorPostgresDriver extends ExPostgresProfile
  with PgDateSupport
  with PgDate2Support
  with PgArraySupport
  with PgLTreeSupport {
  override protected lazy val useTransactionForUpsert = true

  override val api =
    new API with ArrayImplicits with LTreeImplicits with DateTimeImplicits
//      with ByteStringImplicits with ProtoWrappersImplicits
}

object ActorPostgresDriver extends ActorPostgresDriver