import com.softwaremill.macwire._
import controllers.{AuthenticatedAction, AuthenticationController, UserController}
import domain.user.UserRepository
import infrastructure.user.SlickUserRepository
import integration.{AuthenticationService, UserService}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.db.slick.{DbName, DefaultSlickApi, SlickComponents}
import play.api.i18n.I18nComponents
import play.filters.HttpFiltersComponents
import router.Routes
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * This class is the root module for all compile-time dependency injection wiring.
  * It configures Play's built-in components, wires up application code dependencies,
  * and performs initialisation.
  *
  * @see [[https://tinyurl.com/yby9wat3 Compile Time Dependency Injection]]
  * @param context - The application loader Context
  */
class MemqModule(context: Context)
  extends BuiltInComponentsFromContext(context)
    with SlickComponents
    with SlickEvolutionsComponents
    with EvolutionsComponents
    with I18nComponents
    with HttpFiltersComponents
    with controllers.AssetsComponents {

  override lazy val httpFilters = Seq(securityHeadersFilter, allowedHostsFilter)
  override lazy val httpErrorHandler = new MemqErrorHandler
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

  lazy val initializer = wire[MemqInitializer]

  // Run Evolutions
  applicationEvolutions
  // Run Initializer
  initializer

}
