import domain.user.UserData.RegistrationData
import domain.user._
import infrastructure.user.SlickAddressRepository
import play.api.{Environment, Logger, Mode}

import scala.concurrent.ExecutionContext

class MemqInitializer(
  environment: Environment,
  userRepository: UserRepository,
  addresses: SlickAddressRepository,
  implicit val executionContext: ExecutionContext
) {

  if (environment.mode == Mode.Dev) {
    Logger.debug("Beginning Dev mode initialisation")

    Logger.debug("Inserting user seeds...")
    userRepository.save(User.fromRegistrationData(RegistrationData("Jim", "pass"))) map { either =>
      either map (user => addresses.save(Address(None, user.id.get, "123 Fake St.")))
    }
    userRepository.save(User.fromRegistrationData(RegistrationData("Mooba", "pass"))) map { either =>
      either map (user => addresses.save(Address(None, user.id.get, "123 Moob St.")))
    }
  }

}
