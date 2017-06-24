package domain.user

import domain.base.Repository
import domain.user.UserData.SearchData

trait UserRepository extends Repository[User] {

  def findByToken(token: String): DBResult[User]

  def findByCredentials(username: String, password: String): DBResult[User]

  def search(query: SearchData): DBResult[Seq[User]]

}
