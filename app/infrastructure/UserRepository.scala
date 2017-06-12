package infrastructure

import domain.User
import domain.UserData.SearchData
import infrastructure.base.SlickRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext

class UserRepository(
  protected val db: Database,
  implicit val executionContext: ExecutionContext
) extends SlickRepository[UserTable, User] {

  val table = UserTable.table

  private lazy val findByTokenCompiled = Compiled { token: Rep[String] =>
    table.filter(user => user.token.isDefined && user.token === token)
  }

  private lazy val findByCredentialsCompiled = Compiled { (username: Rep[String], password: Rep[String]) =>
    table.filter(_.byCredentials(username, password))
  }

  private lazy val searchCompiled = Compiled { username: Rep[String] =>
    table.filter(_.username.toLowerCase like username)
  }

  def findByToken(token: String): DBResult[User] = runOptional {
    findByTokenCompiled(token).result.headOption
  }

  def findByCredentials(username: String, password: String): DBResult[User] = runOptional {
    findByCredentialsCompiled(username, password).result.headOption
  }

  def search(query: SearchData): DBResult[Seq[User]] = run {
    searchCompiled(query.username.toLowerCase + "%").result
  }

}
