package domain

import java.time.LocalDateTime

import domain.UserData.RegistrationData
import play.api.libs.json._

import scala.util.Random

case class User(
  id: Option[Long],
  username: String,
  password: String,
  token: String,
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

  def createFromData(data: RegistrationData): User = {
    // TODO: Rethink this
    val token = Random.alphanumeric.take(50).mkString
    val now = LocalDateTime.now()
    User(None, data.username, data.password, token, now, now)
  }

}
