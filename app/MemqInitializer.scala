import domain.user.UserData.RegistrationData
import domain.user._
import play.api.{Environment, Logger, Mode}

class MemqInitializer(
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
