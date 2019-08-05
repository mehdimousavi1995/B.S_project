package persist.cassandra.user

case class UserModel (
                     userId: Int,
                     createdAt: Long,
                     firstName: String,
                     lastName: String
                     )