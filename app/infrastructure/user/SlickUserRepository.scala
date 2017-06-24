package infrastructure.user

import domain.user.UserData.SearchData
import domain.user._
import infrastructure.base.SlickRepository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

final class SlickUserRepository(
  protected val config: DatabaseConfig[JdbcProfile],
  implicit val executionContext: ExecutionContext
) extends SlickRepository[UserTable, User] with UserRepository {

  import config.profile.api._

  override protected val table = UserTable.table

  private lazy val findByTokenCompiled = Compiled { token: Rep[String] =>
    table.filter(user => user.token.isDefined && user.token === token)
  }

  private lazy val findByCredentialsCompiled = Compiled {
    (username: Rep[String], password: Rep[String]) =>
      table.filter(_.byCredentials(username, password))
  }

  private lazy val searchCompiled = Compiled { username: Rep[String] =>
    table.filter(_.username.toLowerCase like username)
  }

  override def findByToken(token: String): DBResult[User] = runOptional {
    findByTokenCompiled(token).result.headOption
  }

  override def findByCredentials(username: String, password: String): DBResult[User] = runOptional {
    findByCredentialsCompiled(username, password).result.headOption
  }

  override def search(query: SearchData): DBResult[Seq[User]] = run {
    searchCompiled(query.username.toLowerCase + "%").result
  }

}
