package infrastructure.base

import domain.Entity
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
      case e: Exception => Left(DBError(e))
    }

  protected def runOptional[R](operation: DBIO[Option[R]]): DBResult[R] =
    db run {
      operation map {
        case Some(user) => Right(user)
        case None => Left(NotFoundError())
      }
    } recover {
      case e: Exception => Left(DBError(e))
    }

}
