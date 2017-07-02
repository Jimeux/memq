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

  override protected val table: TableQuery[UserTable] = UserTable.table

  override def findByToken(token: String): DBResult[User] = runOptional {
    table.byToken(token).result.headOption
  }

  override def findByCredentials(username: String, password: String): DBResult[User] = runOptional {
    table.byCredentials(username, password).result.headOption
  }

  override def search(query: SearchData): DBResult[Seq[User]] = run {
    table.search(query.username.toLowerCase + "%").result
  }

}
