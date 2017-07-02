package infrastructure.base

import java.sql.Timestamp
import java.time.LocalDateTime
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._

object ColumnMappers {

  implicit val localDateTimeColumnType: JdbcType[LocalDateTime] with BaseTypedType[LocalDateTime] =
    MappedColumnType.base[LocalDateTime, Timestamp](Timestamp.valueOf, _.toLocalDateTime)

}
