package infrastructure.base

import java.sql.SQLTransientConnectionException

import domain._
import org.postgresql.util.PSQLException
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.CompiledFunction

import scala.concurrent.{ExecutionContext, Future}

trait SlickRepository[T <: SlickTable[E], E <: Entity]
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  implicit val executionContext: ExecutionContext

  type DBResult[R] = Future[Either[DBError, R]]

  val table: TableQuery[T]

  lazy protected val saveCompiled = table returning table

  lazy protected val findOneCompiled: CompiledFunction[(Rep[Long]) => Query[T, E, Seq], Rep[Long], Long, Query[T, E, Seq], Seq[E]] =
    Compiled { id: Rep[Long] =>
      table.filter(_.id === id)
    }

  lazy protected val findPageCompiled = Compiled { (offset: ConstColumn[Long], limit: ConstColumn[Long]) =>
    table.drop(offset).take(limit)
  }

  def save(entity: E): DBResult[E] = run {
    saveCompiled += entity
  }

  def update(entity: E): DBResult[E] = runOptional {
    findOneCompiled(entity.id.get).update(entity) map {
      case 0 => None // ID not found
      case _ => Some(entity)
    }
  }

  def findOne(id: Long): DBResult[E] = runOptional {
    findOneCompiled(id).result.headOption
  }

  def findAll(offset: Long, limit: Long): DBResult[Seq[E]] = run {
    findPageCompiled(offset, limit).result
  }

  protected def run[R](operation: DBIO[R]): DBResult[R] =
    db run {
      operation map (Right(_))
    } recover {
      case e: Exception => Left(toDBError(e))
    }

  protected def runOptional[R](operation: DBIO[Option[R]]): DBResult[R] =
    db run {
      operation map {
        case Some(entity) => Right(entity)
        case None => Left(NotFoundError)
      }
    } recover {
      case e: Exception => Left(toDBError(e))
    }

  /**
    * https://www.postgresql.org/docs/8.1/static/errcodes-appendix.html
    */
  private final val UniqueViolationCode = "23505"
  /**
    * Extract "Jim" from "Detail: Key (username)=(Jim) already exists."
    */
  private final val UniqueFieldPattern = "(?s).*=\\((\\w+)\\).*".r

  protected def toDBError(e: Exception): DBError = e match {
    case e: PSQLException if e.getSQLState == UniqueViolationCode =>
      Logger.debug(s"Handled UniqueViolationError caused by: ${e.getMessage}")
      UniqueViolationError(extractField(e.getMessage))
    case e: SQLTransientConnectionException =>
      Logger.debug(s"Handled ConnectionError caused by: ${e.getMessage}")
      ConnectionError
    case _ =>
      Logger.error(s"An unhandled database error occurred with cause: ${e.getMessage}")
      UnhandledDBError
  }

  private def extractField(message: String): String = {
    message match {
      case UniqueFieldPattern(found) => found
      case _ => "Field"
    }
  }

}
