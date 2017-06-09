package domain

import java.time.LocalDateTime

import domain.UserData.RegistrationData
import play.api.libs.json._

case class User(
  id: Option[Long],
  username: String,
  password: String,
  token: Option[String],
  createdAt: LocalDateTime,
  modified: LocalDateTime
) extends Entity with Timestamps

object User {
  import play.api.data.Forms._

  implicit val userReads: Reads[User] = Json.reads[User]

  implicit val userWrites: Writes[User] = (user: User) => Json.obj(
    "id" -> user.id.get,
    "username" -> user.username,
    "createdAt" -> user.createdAt,
    "modified" -> user.modified
  )

  final val usernameValidations = text(minLength = 5, maxLength = 100)
  final val passwordValidations = text(minLength = 6, maxLength = 120)

  def fromRegistrationData(data: RegistrationData): User = {
    val now = LocalDateTime.now()
    User(None, data.username, data.password, None, now, now)
  }

}
