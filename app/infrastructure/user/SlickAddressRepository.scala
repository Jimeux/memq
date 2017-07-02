package infrastructure.user

import domain.user.Address
import infrastructure.base.SlickRepository
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

final class SlickAddressRepository(
  protected val config: DatabaseConfig[JdbcProfile],
  implicit val executionContext: ExecutionContext
) extends SlickRepository[AddressTable, Address] {
  override protected val table = AddressTable.table
}
