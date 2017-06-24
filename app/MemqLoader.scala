import play.api.ApplicationLoader.Context
import play.api.{Application, ApplicationLoader, LoggerConfigurator}

/**
  * This class acts as the application entry point, and initialises
  * compile-time dependency injection and other configuration.
  *
  * @see [[https://tinyurl.com/yby9wat3 Compile Time Dependency Injection]]
  */
class MemqLoader extends ApplicationLoader {

  def load(context: Context): Application = {
    initLogger(context)
    new MemqModule(context).application
  }

  def initLogger(context: Context): Unit = {
    LoggerConfigurator(context.environment.classLoader) foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
  }

}
