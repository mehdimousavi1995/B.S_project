package helpers

import scala.collection.mutable.ListBuffer

trait Converter {

  val convertPersianNumberToEnglishNumber: PartialFunction[Char, Char] = {
    case '۱' ⇒ '1'
    case '۲' ⇒ '2'
    case '۳' ⇒ '3'
    case '۴' ⇒ '4'
    case '۵' ⇒ '5'
    case '۶' ⇒ '6'
    case '۷' ⇒ '7'
    case '۸' ⇒ '8'
    case '۹' ⇒ '9'
    case '۰' ⇒ '0'
    case e   ⇒ e
  }

  val convertEnglishNumberToPersianNumber: PartialFunction[Char, Char] = {
    case '1' ⇒ '۱'
    case '2' ⇒ '۲'
    case '3' ⇒ '۳'
    case '4' ⇒ '۴'
    case '5' ⇒ '۵'
    case '6' ⇒ '۶'
    case '7' ⇒ '۷'
    case '8' ⇒ '۸'
    case '9' ⇒ '۹'
    case '0' ⇒ '۰'
    case e   ⇒ e
  }

  def convertEnglishStringToPersian(str: String): String =
    str map convertEnglishNumberToPersianNumber

  def makeNumberStandard(num: String): String =
    num map convertPersianNumberToEnglishNumber

  def toJson(o: Any): String = {
    var json = new ListBuffer[String]()
    o match {
      case m: Map[_, _] ⇒ {
        for ((k, v) ← m) {
          var key = escape(k.asInstanceOf[String])
          v match {
            case a: Map[_, _] ⇒ json += "\"" + key + "\":" + toJson(a)
            case a: List[_]   ⇒ json += "\"" + key + "\":" + toJson(a)
            case a: Int       ⇒ json += "\"" + key + "\":" + a
            case a: Boolean   ⇒ json += "\"" + key + "\":" + a
            case a: String    ⇒ json += "\"" + key + "\":\"" + escape(a) + "\""
            case _            ⇒ ;
          }
        }
      }
      case m: List[_] ⇒ {
        var list = new ListBuffer[String]()
        for (el ← m) {
          el match {
            case a: Map[_, _] ⇒ list += toJson(a)
            case a: List[_]   ⇒ list += toJson(a)
            case a: Int       ⇒ list += a.toString()
            case a: Boolean   ⇒ list += a.toString()
            case a: String    ⇒ list += "\"" + escape(a) + "\""
            case _            ⇒ ;
          }
        }
        return "[" + list.mkString(",") + "]"
      }
      case _ ⇒ ;
    }
    return "{" + json.mkString(",") + "}"
  }

  def escape(s: String): String = {
    return s.replaceAll("\"", "\\\\\"");
  }
}

