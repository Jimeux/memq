import com.google.inject.AbstractModule

class Module extends AbstractModule {

  override def configure(): Unit = {
    initialize()
  }

  private def initialize(): Unit = {
    bind(classOf[Initializer]).asEagerSingleton()
  }

}
