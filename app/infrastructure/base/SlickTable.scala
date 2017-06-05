package infrastructure.base

import java.sql.Timestamp
import java.time.LocalDateTime

import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._

abstract class SlickTable[T](tag: Tag, tableName: String)
  extends Table[T](tag, tableName) {

  implicit val localDateTimeColumnType: JdbcType[LocalDateTime] with BaseTypedType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, Timestamp](Timestamp.valueOf, _.toLocalDateTime)

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

}
