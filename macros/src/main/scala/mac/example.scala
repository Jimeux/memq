package mac

import scala.meta._
import org.scalameta.logger

class example extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {
    val q"trait $name extends $tpe" = defn
    defn
  }

}

