package integration

import javax.inject.{Inject, Singleton}

import domain.User
import domain.UserData.{AuthenticationData, RegistrationData, SearchData}
import infrastructure.UserRepository
import integration.base.{BaseService, JsResponse}
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(
  users: UserRepository,
  messages: MessagesApi,
  authenticationService: AuthenticationService,
  executionContext: ExecutionContext
) extends BaseService(messages, executionContext) {

  def find(id: Long): Future[JsResponse] = users.findOne(id) map (toResponse(_))

  def findAll(page: Int): Future[JsResponse] =
    users.findAll(DefaultOffset(page), DefaultPerPage) map (toResponse(_))

  def register(data: RegistrationData): Future[JsResponse] = {
    users.save(User.fromRegistrationData(data)) flatMap {
      case Right(user) =>
        val authenticationData = AuthenticationData(user.username, user.password)
        authenticationService.authenticate(authenticationData)
      case Left(error) =>
        Future.successful(toJsError(error))
    }
  }

  def search(data: SearchData): Future[JsResponse] = users.search(data) map (toResponse(_))

}
