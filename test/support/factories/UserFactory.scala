package support.factories

import java.time.LocalDateTime

import domain.User

class UserFactory {

  private var defaultId = 1

  def build(id: Option[Long] = Some(defaultId),
            username: String = s"Username$defaultId",
            password: String = s"Password$defaultId",
            token: Option[String] = Some(s"Token$defaultId"),
            createdAt: LocalDateTime = LocalDateTime.now,
            modified: LocalDateTime = LocalDateTime.now
  ): User = {
    defaultId += 1
    User(id, username, password, token, createdAt, modified)
  }

}
