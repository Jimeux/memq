package domain.base

import scala.concurrent.Future

trait Repository[E] {

  type DBResult[R] = Future[Either[DBError, R]]

  def save(entity: E): DBResult[E]

  def update(entity: E): DBResult[E]

  def findOne(id: Long): DBResult[E]

  def findAll(offset: Long, limit: Long): DBResult[Seq[E]]

}
