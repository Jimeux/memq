import com.softwaremill.macwire._
import controllers.{AuthenticatedAction, AuthenticationController, UserController}
import domain.user.UserRepository
import infrastructure.user.{SlickAddressRepository, SlickUserRepository}
import integration.{AuthenticationService, UserService}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.db.slick.{DbName, DefaultSlickApi, SlickApi, SlickComponents}
import play.api.http.HttpErrorHandler
import play.api.i18n.I18nComponents
import play.api.mvc.EssentialFilter
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

  override lazy val httpFilters: Seq[EssentialFilter] = Seq(securityHeadersFilter, allowedHostsFilter)
  override lazy val httpErrorHandler: HttpErrorHandler = new MemqErrorHandler
  override lazy val slickApi: SlickApi = new DefaultSlickApi(environment, configuration, applicationLifecycle)(executionContext)
  override lazy val dynamicEvolutions: DynamicEvolutions = new DynamicEvolutions

  lazy val dbConfig: DatabaseConfig[JdbcProfile] = slickApi.dbConfig[JdbcProfile](DbName("memq"))

  lazy val userRepository: UserRepository = wire[SlickUserRepository]
  lazy val addressRepository: SlickAddressRepository = wire[SlickAddressRepository]

  lazy val authenticationService: AuthenticationService = wire[AuthenticationService]
  lazy val userService: UserService = wire[UserService]

  lazy val authenticatedAction: AuthenticatedAction = wire[AuthenticatedAction]

  lazy val userController: UserController = wire[UserController]
  lazy val authenticationController: AuthenticationController = wire[AuthenticationController]

  lazy val router: Routes = new Routes(
    httpErrorHandler,
    userController,
    authenticationController,
    assets
  )

  lazy val initializer: MemqInitializer = wire[MemqInitializer]

  // Run Evolutions
  applicationEvolutions
  // Run Initializer
  initializer

}
