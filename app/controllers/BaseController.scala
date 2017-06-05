package controllers

import integration.base._
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

abstract class BaseController(
  components: ControllerComponents,
  executionContext: ExecutionContext
) extends AbstractController(components) {

  implicit val ec = executionContext

  /**
    * Convert a JSON representation of a response to an HTTP response.
    *
    * @param response a response object containing JSON data
    * @return an HTTP response containing the JSON
    */
  protected def toResult(response: JsResponse): Result = response match {
    case JsSuccessful(json)   => Ok(json)
    case JsNotFound(json)     => NotFound(json)
    case JsInvalid(json)      => NotAcceptable(json)
    case JsUnknown(json)      => InternalServerError(json)
    case JsForbidden(json)    => Forbidden(json)
    case JsUnauthorized(json) => Unauthorized(json)
  }

  /**
    * Convert a form containing errors to an HTTP response.
    *
    * @param invalidForm form data parsed from an invalid request
    * @tparam T the data type containing the request data
    * @return an HTTP response
    */
  protected def toResult[T](invalidForm: Form[T]): Future[Result] = {
    // TODO: Consider using MessagesApi
    val errors = invalidForm.errors map (e => s"${e.key}: ${e.message}")
    Future.successful(NotAcceptable(Json.toJson(errors)))
  }

}
