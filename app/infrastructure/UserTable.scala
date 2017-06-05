package infrastructure

import java.time.LocalDateTime

import domain.User
import infrastructure.base.SlickTable
import slick.jdbc.PostgresProfile.api._

class UserTable(tag: Tag) extends SlickTable[User](tag, "users") {
  def username  = column[String]("username")
  def password  = column[String]("password")
  def token     = column[String]("token")
  def createdAt = column[LocalDateTime]("created_at")
  def modified  = column[LocalDateTime]("modified")

  def * = (
    id.?, username, password, token, createdAt, modified
  ) <> ((User.apply _).tupled, User.unapply)

  def byCredentials(aUsername: Rep[String], aPassword: Rep[String]): Rep[Boolean] =
    username === aUsername && password === aPassword

}

object UserTable {
  lazy val table = TableQuery[UserTable]
}
