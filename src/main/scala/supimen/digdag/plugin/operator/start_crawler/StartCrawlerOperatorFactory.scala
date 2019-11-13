package supimen.digdag.plugin.operator.start_crawler

import io.digdag.spi.{Operator, OperatorContext, OperatorFactory, TemplateEngine}

class StartCrawlerOperatorFactory(templateEngine: TemplateEngine) extends OperatorFactory {
  override def getType: String = "glue.start_crawler"

  override def newOperator(context: OperatorContext): Operator =
    new StartCrawlerOperator(context)
}
