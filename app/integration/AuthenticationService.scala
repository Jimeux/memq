package integration

import javax.inject.{Inject, Singleton}

import cats.data._
import cats.implicits._
import domain.User
import domain.UserData.AuthenticationData
import infrastructure.UserRepository
import integration.base.{BaseService, JsResponse}
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
class AuthenticationService @Inject()(
  users: UserRepository,
  messages: MessagesApi,
  executionContext: ExecutionContext
) extends BaseService(messages, executionContext) {

  def verifyToken(token: String): Future[Option[User]] =
    users.findByToken(token) map (_.toOption)

  def authenticate(data: AuthenticationData): Future[JsResponse] = {
    val json = for {
      found <- EitherT(users.findByCredentials(data.username, data.password))
      updated = found.copy(token = generateToken)
      saved <- EitherT(users.update(updated))
    } yield JsObject(Seq(
      "token" -> JsString(saved.token),
      "user" -> Json.toJson(saved)
    ))

    json.value map (toResponse(_))
  }

  // TODO: Use more secure method, e.g. https://github.com/pauldijou/jwt-scala
  private def generateToken: String = Random.alphanumeric.take(50).mkString

}
