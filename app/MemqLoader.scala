import controllers.{AuthenticatedAction, AuthenticationController, UserController}
import infrastructure.UserRepository
import integration.{AuthenticationService, UserService}
import play.api.ApplicationLoader.Context
import play.api.db.slick.{DbName, SlickComponents}
import play.api.i18n._
import play.api.mvc.EssentialFilter
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import slick.jdbc.{JdbcBackend, JdbcProfile}
import router.Routes

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
    with I18nComponents
    with HttpFiltersComponents
    with SlickComponents
    with controllers.AssetsComponents {

  override lazy val httpFilters: Seq[EssentialFilter] = Seq(securityHeadersFilter, allowedHostsFilter)

  lazy val db: JdbcBackend#DatabaseDef = slickApi.dbConfig[JdbcProfile](DbName("default")).db

  lazy val userRepository: UserRepository = new UserRepository(db, executionContext)
  lazy val authenticationService: AuthenticationService = new AuthenticationService(userRepository, messagesApi, configuration, executionContext)
  lazy val userService: UserService = new UserService(userRepository, messagesApi, authenticationService, executionContext)
  lazy val authenticatedAction: AuthenticatedAction = new AuthenticatedAction(playBodyParsers, authenticationService, executionContext)

  lazy val userController: UserController = new UserController(controllerComponents, userService, authenticatedAction, executionContext)
  lazy val authenticationController: AuthenticationController = new AuthenticationController(controllerComponents, authenticationService, authenticatedAction, executionContext)

  lazy val router: Routes = new Routes(
    httpErrorHandler,
    userController,
    authenticationController,
    assets
  )

}
