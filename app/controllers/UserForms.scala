package controllers

import domain.user.UserData._
import domain.user.User
import play.api.data.Form
import play.api.data.Forms._

object UserForms {

  val AuthenticationForm = Form(
    mapping(
      "username" -> User.usernameValidations,
      "password" -> User.passwordValidations
    )(AuthenticationData.apply)(AuthenticationData.unapply)
  )

  val RegistrationForm = Form(
    mapping(
      "username" -> User.usernameValidations,
      "password" -> User.passwordValidations
    )(RegistrationData.apply)(RegistrationData.unapply)
  )

  val SearchForm = Form(
    mapping("username" -> nonEmptyText)(SearchData.apply)(SearchData.unapply)
  )

}
