package infrastructure

import javax.inject.{Inject, Singleton}

import domain.User
import domain.UserData.SearchData
import infrastructure.base.SlickRepository
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext

@Singleton
class UserRepository @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  implicit val executionContext: ExecutionContext
) extends SlickRepository[UserTable, User] {

  import dbConfig.profile.api._

  val table = UserTable.table

  private lazy val findByTokenCompiled = Compiled { token: Rep[String] =>
    table.filter(_.token === token)
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
