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

  import com.softwaremill.macwire._

  override lazy val httpFilters = Seq(securityHeadersFilter, allowedHostsFilter)

  lazy val db = slickApi.dbConfig[JdbcProfile](DbName("default")).db

  lazy val userRepository = wire[UserRepository]
  lazy val authenticationService = wire[AuthenticationService]
  lazy val userService = wire[UserService]
  lazy val authenticatedAction = wire[AuthenticatedAction]

  lazy val userController = wire[UserController]
  lazy val authenticationController = wire[AuthenticationController]

  lazy val router = wire[Routes]

}
