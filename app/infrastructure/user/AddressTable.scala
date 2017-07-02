package infrastructure.user

import domain.user.Address
import infrastructure.base.SlickTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

final class AddressTable(tag: Tag) extends SlickTable[Address](tag, "addresses") {
  def userId: Rep[Long] = column[Long]("user_id")
  def value: Rep[String] = column[String]("value")

  def * : ProvenShape[Address] = (id.?, userId, value) <> ((Address.apply _).tupled, Address.unapply)

}

object AddressTable {
  lazy val table: TableQuery[AddressTable] = TableQuery[AddressTable]
}
