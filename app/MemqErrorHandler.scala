import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future

class MemqErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(Status(statusCode)(s"A client error [code $statusCode] occurred: $message"))
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Logger.error(s"Caught error in onServerError: ${exception.getMessage}")
    Future.successful(InternalServerError)
  }

}

