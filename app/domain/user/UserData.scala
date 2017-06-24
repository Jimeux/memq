package domain.user

object UserData {

  case class AuthenticationData(username: String, password: String)

  case class RegistrationData(username: String, password: String)

  case class SearchData(username: String)

}