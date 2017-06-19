package infrastructure.base

import java.sql.SQLTransientConnectionException

import domain._
import org.postgresql.util.PSQLException
import play.api.Logger

object PostgresErrorHandler {

  /**
    * https://www.postgresql.org/docs/8.1/static/errcodes-appendix.html
    */
  private final val UniqueViolationCode = "23505"
  /**
    * Extract "Jim" from "Detail: Key (username)=(Jim) already exists."
    */
  private final val UniqueFieldPattern = "(?s).*=\\((\\w+)\\).*".r

  def toDBError(e: Exception): DBError = e match {
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
