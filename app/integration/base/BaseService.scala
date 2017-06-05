package integration.base

import infrastructure.base._
import play.api.i18n.MessagesApi
import play.api.libs.json._

import scala.concurrent.ExecutionContext

abstract class BaseService(
  messages: MessagesApi,
  executionContext: ExecutionContext
) {

  implicit val ec = executionContext

  final val DefaultPerPage = 2
  final val DefaultOffset = (page: Int) => DefaultPerPage * page - DefaultPerPage

  /**
    * Converts an infrastructure result to a JSON response.
    *
    * @note When mapping with this method, use (toResponse(_)), since
    *       implicit resolution seems to fail with a simple method reference.
    *
    * @param result - A returned result from the infrastructure layer
    * @tparam T - The result type, which must have an associated JSON representation
    * @return The appropriate JSON representation of the infrastructure result
    */
  protected def toResponse[T: Writes](result: Either[DBError, T]): JsResponse = {
    result match {
      case Right(data) => JsSuccessful(Json.toJson(data))
      case Left(error) => toJsError(error)
    }
  }

  /**
    * Maps a DB error type to its JSON representation with appropriate message.

    * @param error - An object representing an error encountered during a query
    * @return A JSON response representing an error
    */
  protected def toJsError(error: DBError): JsResponse = {
    error match {
      case NotFoundError() => JsNotFound(msg("json.error.not_found"))
      case UniqueViolationError(field) => JsInvalid(msg("json.error.unique", field))
      case ConnectionError() => JsUnknown(msg("json.error.connection"))
      case UnhandledDBError() => JsUnknown(msg("json.error.unknown"))
    }
  }

  private def msg(key: String, args: Any*): JsString = {
    JsString(messages(key, args: _*))
  }

}
