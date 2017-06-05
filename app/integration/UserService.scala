package integration

import javax.inject.{Inject, Singleton}

import domain.User
import domain.UserData.{RegistrationData, SearchData}
import infrastructure.UserRepository
import integration.base.{BaseService, JsResponse}
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(
  users: UserRepository,
  messages: MessagesApi,
  executionContext: ExecutionContext
) extends BaseService(messages, executionContext) {

  def find(id: Long): Future[JsResponse] = users.findOne(id) map (toResponse(_))

  def findAll(page: Int): Future[JsResponse] =
    users.findAll(DefaultOffset(page), DefaultPerPage) map (toResponse(_))

  def register(data: RegistrationData): Future[JsResponse] =
    users.save(User.createFromData(data)) map (toResponse(_))

  def search(data: SearchData): Future[JsResponse] =
    users.search(data) map (toResponse(_))

}
