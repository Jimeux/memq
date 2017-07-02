package integration

import domain.user._
import domain.user.UserData._
import integration.base.{BaseService, JsResponse}
import play.api.i18n.{Langs, MessagesApi}

import scala.concurrent.{ExecutionContext, Future}

final class UserService(
  users: UserRepository,
  langs: Langs,
  messages: MessagesApi,
  authenticationService: AuthenticationService,
  executionContext: ExecutionContext
) extends BaseService(langs, messages, executionContext) {

  def find(id: Long): Future[JsResponse] =
    users.findOne(id) map (toResponse(_))

  def findAll(page: Int): Future[JsResponse] =
    users.findAll(PageOffset(page), DefaultPerPage) map (toResponse(_))

  def register(data: RegistrationData): Future[JsResponse] =
    users.save(User.fromRegistrationData(data)) flatMap {
      case Right(user) =>
        val authenticationData = AuthenticationData(user.username, user.password)
        authenticationService.authenticate(authenticationData)
      case Left(error) =>
        Future.successful(toJsError(error))
    }

  def search(data: SearchData): Future[JsResponse] =
    users.search(data) map (toResponse(_))

  def addresses(page: Int): Future[JsResponse] =
    users.withAddress(PageOffset(page), DefaultPerPage) map (toResponse(_))

}
