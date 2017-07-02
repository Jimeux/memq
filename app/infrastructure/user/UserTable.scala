package infrastructure.user

import java.time.LocalDateTime

import domain.user._
import infrastructure.base.SlickTable
import infrastructure.base.ColumnMappers._
import slick.jdbc.PostgresProfile.api._

final class UserTable(tag: Tag) extends SlickTable[User](tag, "users") {
  def username: Rep[String] = column[String]("username")
  def password: Rep[String] = column[String]("password")
  def token: Rep[Option[String]] = column[Option[String]]("token")
  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
  def modified: Rep[LocalDateTime] = column[LocalDateTime]("modified")

  def * = (
    id.?, username, password, token, createdAt, modified
  ) <> ((User.apply _).tupled, User.unapply)

  def byCredentials(aUsername: Rep[String], aPassword: Rep[String]): Rep[Boolean] =
    username === aUsername && password === aPassword

}

object UserTable {

  lazy val table: TableQuery[UserTable] = TableQuery[UserTable]

  implicit class QueryExtensions(users: TableQuery[UserTable]) {
    lazy val byToken = Compiled { token: Rep[String] =>
      users.filter(u => u.token.isDefined && u.token === token)
    }

    lazy val byCredentials = Compiled { (username: Rep[String], password: Rep[String]) =>
      users.filter(u => u.username === username && u.password === password)
    }

    lazy val search = Compiled { username: Rep[String] =>
      users.filter(_.username.toLowerCase like username)
    }
  }

}
