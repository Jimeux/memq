package infrastructure.user

import java.time.LocalDateTime

import domain.user._
import infrastructure.base.SlickTable
import infrastructure.base.ColumnMappers._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

final class UserTable(tag: Tag) extends SlickTable[User](tag, "users") {
  def username: Rep[String] = column[String]("username")
  def password: Rep[String] = column[String]("password")
  def token: Rep[Option[String]] = column[Option[String]]("token")
  def createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
  def modified: Rep[LocalDateTime] = column[LocalDateTime]("modified")

  def address = foreignKey("user_address_fk", id, AddressTable.table)(_.userId)

  def * : ProvenShape[User] = (id.?, username, password, token, createdAt, modified) <> (pack, unpack)

  type Row = (Option[Long], String, String, Option[String], LocalDateTime, LocalDateTime)

  def pack(r: Row): User = User(r._1, r._2, r._3, r._4, r._5, r._6)

  def unpack(u: User): Option[Row] = Some(u.id, u.username, u.password, u.token, u.createdAt, u.modified)

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

    lazy val findAllWithAddress = Compiled { (offset: ConstColumn[Long], limit: ConstColumn[Long]) =>
      (for {
        u <- table
        a <- u.address
      } yield (u, a))
        .drop(offset)
        .take(limit)
    }
  }

}
