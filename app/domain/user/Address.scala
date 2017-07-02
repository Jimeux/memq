package domain.user

import domain.base.Entity
import play.api.libs.json.{Json, Writes}

final case class Address(
  id: Option[Long],
  userId: Long,
  value: String
) extends Entity

object Address {
  implicit val addressWrites: Writes[Address] = Json.writes[Address]
}
