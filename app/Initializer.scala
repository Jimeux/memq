import com.google.inject.{Inject, Singleton}
import domain.User
import domain.UserData.RegistrationData
import infrastructure.UserRepository
import play.api.{Environment, Logger, Mode}

@Singleton
class Initializer @Inject()(
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
