import domain.User
import domain.UserData.RegistrationData
import infrastructure.UserRepository
import play.api.{Environment, Logger, Mode}

// TODO: Integrate with MemqLoader
class Initializer(
  environment: Environment,
  userRepository: UserRepository
) {

  if (environment.mode == Mode.Dev) {
    Logger.debug("Beginning Dev mode initialisation")

    Logger.debug("Inserting user seeds...")
    userRepository.save(User.fromRegistrationData(RegistrationData("Jim", "pass")))
    userRepository.save(User.fromRegistrationData(RegistrationData("Mooba", "pass")))
  }

}
