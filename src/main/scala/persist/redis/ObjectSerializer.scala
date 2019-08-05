package persist.redis

import spray.json.{JsonReader, JsonWriter}
import spray.json._

object ObjectSerializer {

  def serialize[T](obj: T)(implicit jsonWriter: JsonWriter[T]): String =
    obj.toJson.toString()

  def deSerialize[T](str: String)(implicit jsonReader: JsonReader[T]): T =
    str.parseJson.convertTo[T]

}
