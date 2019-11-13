package supimen.digdag.plugin

import io.digdag.spi._
import supimen.digdag.plugin.operator.GlueOperatorProvider

class GluePlugin extends Plugin {
  override def getServiceProvider[T](`type`: Class[T]): Class[_ <: T] =
    if (`type` eq classOf[OperatorProvider])
      classOf[GlueOperatorProvider].asSubclass(`type`)
    else null
}
