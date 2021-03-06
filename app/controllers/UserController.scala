package controllers

import controllers.UserForms._
import integration._
import play.api.mvc._

class UserController(
  userService: UserService,
  components: ControllerComponents,
  authenticated: AuthenticatedAction
) extends BaseController(components) {

  def index(page: Int) = Action.async(parse.default) { _ =>
    userService.findAll(page) map toResult
  }

  def addresses(page: Int) = Action.async(parse.default) { a =>
    userService.addresses(page) map toResult
  }

  def show(id: Long) = authenticated.async(parse.default) { _ =>
    userService.find(id) map toResult
  }

  def create = Action.async(parse.json) { implicit request =>
    RegistrationForm.bindFromRequest fold (
      errorsToResult,
      userService.register(_) map toResult
    )
  }

  def search = authenticated.async(parse.json) { implicit request =>
    SearchForm.bindFromRequest fold (
      errorsToResult,
      userService.search(_) map toResult
    )
  }

}
