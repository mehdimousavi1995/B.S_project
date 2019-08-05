package helpers

trait RedisKeys {

  private val EmbassyAppointment = "embassy-appointment"
  private val EmbassyAppointmentAdmin = "embassy_appointment_admin"

  def sKey(chatId: String): String = s"state-$EmbassyAppointment-$chatId"

  def admin_s_key(chatId: String) = s"state-$EmbassyAppointmentAdmin-$chatId"

  def uKey(chatId: String): String = s"user-info-$EmbassyAppointment-$chatId"

}
