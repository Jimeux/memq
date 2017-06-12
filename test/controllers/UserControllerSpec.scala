package controllers

import org.scalatestplus.play._
import support.factories.UserFactory

class UserControllerSpec extends PlaySpec with OneAppPerTest {

  val userFactory = new UserFactory

  "User" must {

    "have a token" in {
      val token = Some("token")
      userFactory.build(token = token).token mustBe token
    }

    "have a unique ID" in {
      val user1 = userFactory.build()
      val user2 = userFactory.build()
      user1.id mustNot be(user2.id)
    }

  }

}
