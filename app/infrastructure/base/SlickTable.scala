package infrastructure.base

import slick.jdbc.PostgresProfile.api._

abstract class SlickTable[T](tag: Tag, tableName: String)
  extends Table[T](tag, tableName) {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

}
