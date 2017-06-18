package support.builders

import java.time.LocalDateTime
import nyaya.gen._

import domain.User

object BuildUser {
  def apply(id: Option[Long] = Some(Gen.positiveLong.sample),
            username: String = Gen.alphaNumeric.string(6 to 100).sample,
            password: String = Gen.alphaNumeric.string(6 to 100).sample,
            token: Option[String] = Gen.string(100).option.sample,
            createdAt: LocalDateTime = LocalDateTime.now,
            modified: LocalDateTime = LocalDateTime.now
  ): User = {
    User(id, username, password, token, createdAt, modified)
  }
}
