package infrastructure.base

import domain.base.{DBErrorHandler, Entity, NotFoundError, Repository}
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.concurrent.ExecutionContext

trait SlickRepository[T <: SlickTable[E], E <: Entity] extends Repository[E] {

  /**
    * Necessary for running action combinators with custom functions (map, flatMap, etc.)
    * @see http://slick.lightbend.com/doc/3.2.0/dbio.html#sequential-execution
    */
  implicit val executionContext: ExecutionContext

  /**
    * Convert database-specific exceptions to domain-specific errors
    */
  protected val errorHandler: DBErrorHandler = PostgresErrorHandler

  /**
    * Must be overridden with database-specific Slick configuration
    */
  protected val config: DatabaseConfig[JdbcProfile]

  /**
    * The database instance to use for queries
    */
  protected val db: JdbcBackend#DatabaseDef = config.db

  /**
    * Import implicits and aliases from the profile in the specified config
    */
  import config.profile.api._

  /**
    * Must be overridden with repository-specific TableQuery instance
    */
  protected val table: TableQuery[T]


  lazy protected val saveCompiled = table returning table

  lazy protected val findOneCompiled = Compiled { id: Rep[Long] =>
    table.filter(_.id === id)
  }

  lazy protected val findPageCompiled = Compiled { (offset: ConstColumn[Long], limit: ConstColumn[Long]) =>
    table.drop(offset).take(limit)
  }

  override def save(entity: E): DBResult[E] = run {
    saveCompiled += entity
  }

  override def update(entity: E): DBResult[E] = runOptional {
    findOneCompiled(entity.id.get).update(entity) map {
      case 0 => None // ID not found
      case _ => Some(entity)
    }
  }

  override def findOne(id: Long): DBResult[E] = runOptional {
    findOneCompiled(id).result.headOption
  }

  override def findAll(offset: Long, limit: Long): DBResult[Seq[E]] = run {
    findPageCompiled(offset, limit).result
  }

  /**
    * Execute a DBIO query and convert the result to a DBResult.
    *
    * @param operation - the DBIO query to be executed
    * @tparam R - the type returned by the DBIO query
    * @return either the query result or a domain-specific error
    */
  protected def run[R](operation: DBIO[R]): DBResult[R] =
    db run {
      operation map (Right(_))
    } recover {
      case e: Exception => Left(errorHandler.toDBError(e))
    }

  /**
    * Execute a DBIO query that will return an optional result,
    * and convert that result to a DBResult.
    *
    * @param operation - the DBIO query to be executed
    * @tparam R - the type optionally returned by the DBIO query
    * @return either the query result if non-empty or a domain-specific error
    */
  protected def runOptional[R](operation: DBIO[Option[R]]): DBResult[R] =
    db run {
      operation map {
        case Some(entity) => Right(entity)
        case None => Left(NotFoundError)
      }
    } recover {
      case e: Exception => Left(errorHandler.toDBError(e))
    }

}
