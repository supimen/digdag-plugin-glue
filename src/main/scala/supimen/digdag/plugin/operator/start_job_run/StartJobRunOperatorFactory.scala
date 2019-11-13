package supimen.digdag.plugin.operator.start_job_run

import io.digdag.spi.{Operator, OperatorContext, OperatorFactory, TemplateEngine}

class StartJobRunOperatorFactory(templateEngine: TemplateEngine) extends OperatorFactory {
  override def getType: String = "glue.start_job_run"

  override def newOperator(context: OperatorContext): Operator =
    new StartJobRunOperator(context)
}
