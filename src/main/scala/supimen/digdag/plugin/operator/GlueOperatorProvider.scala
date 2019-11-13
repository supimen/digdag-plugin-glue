package supimen.digdag.plugin.operator

import java.util

import io.digdag.spi.{OperatorFactory, OperatorProvider, TemplateEngine}
import javax.inject.Inject
import supimen.digdag.plugin.operator.start_crawler.StartCrawlerOperatorFactory
import supimen.digdag.plugin.operator.start_job_run.StartJobRunOperatorFactory

class GlueOperatorProvider @Inject()(templateEngine: TemplateEngine) extends OperatorProvider {
  override def get(): util.List[OperatorFactory] = util.Arrays.asList(
    new StartJobRunOperatorFactory(templateEngine),
    new StartCrawlerOperatorFactory(templateEngine)
  )
}
