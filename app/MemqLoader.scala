import controllers.{AuthenticatedAction, AuthenticationController, UserController}
import domain.user.UserRepository
import infrastructure.user.SlickUserRepository
import integration.{AuthenticationService, UserService}
import play.api.ApplicationLoader.Context
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.db.slick.{DbName, DefaultSlickApi, SlickApi, SlickComponents}
import play.api.i18n._
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import router.Routes
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

class MemqLoader extends ApplicationLoader {

  def load(context: Context): Application = {
    initLogger(context)
    new AppComponents(context).application
  }

  def initLogger(context: Context): Unit = {
    LoggerConfigurator(context.environment.classLoader) foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
  }

}

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with SlickComponents
    with SlickEvolutionsComponents
    with EvolutionsComponents
    with I18nComponents
    with HttpFiltersComponents
    with controllers.AssetsComponents {

  import com.softwaremill.macwire._

  override lazy val httpFilters = Seq(securityHeadersFilter, allowedHostsFilter)
  override lazy val httpErrorHandler = new ErrorHandler
  override lazy val slickApi = new DefaultSlickApi(environment, configuration, applicationLifecycle)(executionContext)
  override lazy val dynamicEvolutions: DynamicEvolutions = new DynamicEvolutions

  lazy val dbConfig: DatabaseConfig[JdbcProfile] = slickApi.dbConfig[JdbcProfile](DbName("memq"))

  lazy val userRepository: UserRepository = wire[SlickUserRepository]

  lazy val authenticationService = wire[AuthenticationService]
  lazy val userService = wire[UserService]

  lazy val authenticatedAction = wire[AuthenticatedAction]

  lazy val userController = wire[UserController]
  lazy val authenticationController = wire[AuthenticationController]

  lazy val router: Routes = new Routes(
    httpErrorHandler,
    userController,
    authenticationController,
    assets
  )

  lazy val initializer = wire[Initializer]

  def onStart() = {
    applicationEvolutions
    initializer
  }

  onStart()

}
