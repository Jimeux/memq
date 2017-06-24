package controllers

import domain.user.User
import integration.AuthenticationService
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](
  val user: User,
  val request: Request[A]
) extends WrappedRequest[A](request)

/**
  * An Action capable of authenticating a user
  * by a token value in the Authorization header.
  */
class AuthenticatedAction(
  parsers: PlayBodyParsers,
  authenticationService: AuthenticationService)(
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[AuthenticatedRequest, AnyContent] {

  val parser = parsers.default

  private final val AuthorizationHeader = "Authorization"
  private final val BearerPattern = "Bearer (.+)".r

  def invokeBlock[A](request: Request[A],
                     block: AuthenticatedRequest[A] => Future[Result]): Future[Result] =
    extractToken(request) map { token =>
      authenticationService.verifyToken(token) flatMap {
        case Some(user) => block(new AuthenticatedRequest(user, request))
        case None => Future.successful(Results.Forbidden)
      }
    } getOrElse Future.successful(Results.Forbidden)

  private def extractToken[A](request: Request[A]): Option[String] =
    request.headers.get(AuthorizationHeader) match {
      case Some(BearerPattern(token)) => Some(token)
      case _ => None
    }

}
