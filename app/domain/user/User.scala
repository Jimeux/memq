package domain.user

import java.time.LocalDateTime

import domain.base.Entity
import domain.user.UserData.RegistrationData
import play.api.libs.json.{Json, Writes}

final case class User(
  id: Option[Long],
  username: String,
  password: String,
  token: Option[String],
  createdAt: LocalDateTime,
  modified: LocalDateTime,
  address: Option[Address] = None
) extends Entity

object User {

  implicit val userWrites: Writes[User] = Json.writes[User]

  def fromRegistrationData(data: RegistrationData): User = {
    val now = LocalDateTime.now()
    User(None, data.username, data.password, None, now, now)
  }

  import play.api.data.Forms._

  final val usernameValidations = text(minLength = 4, maxLength = 100)
  final val passwordValidations = text(minLength = 4, maxLength = 120)

}
