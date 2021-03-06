package controllers

import controllers.UserForms._
import integration._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticationController(
  components: ControllerComponents,
  authenticationService: AuthenticationService,
  authenticate: AuthenticatedAction
) extends BaseController(components) {

  def login = Action.async(parse.json) { implicit request =>
    AuthenticationForm.bindFromRequest fold (
      _ => Future.successful(Unauthorized),
      authenticationService.authenticate(_) map toResult
    )
  }

}
