package infrastructure.base

import domain._
import infrastructure.base.PostgresErrorHandler._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

trait SlickRepository[T <: SlickTable[E], E <: Entity] {

  type DBResult[R] = Future[Either[DBError, R]]

  implicit val executionContext: ExecutionContext

  protected val db: Database

  val table: TableQuery[T]

  lazy protected val saveCompiled = table returning table

  lazy protected val findOneCompiled = Compiled { id: Rep[Long] =>
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

}
