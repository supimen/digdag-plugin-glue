package supimen.digdag.plugin.operator.start_job_run

import com.amazonaws.services.glue.model.{NotificationProperty, StartJobRunRequest}
import io.digdag.spi.{OperatorContext, TaskResult}
import supimen.digdag.plugin.operator.GlueBaseOperator
import supimen.digdag.plugin.waiter.GlueJobWaiter

class StartJobRunOperator(context: OperatorContext) extends GlueBaseOperator(context) {
  private val jobName               = params.get("job_name", classOf[String])
  private val arguments             = params.getMapOrEmpty("arguments", classOf[String], classOf[String])
  private val timeout               = Option(params.getOptional("timeout", classOf[Integer]).orNull())
  private val workerType            = Option(params.getOptional("worker_type", classOf[String]).orNull())
  private val numberOfWorkers       = Option(params.getOptional("number_of_workers", classOf[Integer]).orNull())
  private val notifyDelayAfter      = Option(params.getOptional("notify_delay_after", classOf[Integer]).orNull())
  private val maxCapacity           = Option(params.getOptional("max_capacity", classOf[Double]).orNull())
  private val securityConfiguration = Option(params.getOptional("security_configuration", classOf[String]).orNull())

  override def runTask(): TaskResult = {
    val glue               = glueClient(context)
    val startJobRunRequest = startJobRunRequestWithParams(new StartJobRunRequest())
    logger.info(startJobRunRequest.toString)

    val jobRunId = glue.startJobRun(startJobRunRequest).getJobRunId

    logger.info(s"Running JobRunId:$jobRunId.")

    new GlueJobWaiter(glue)
      .waitJobRun(jobRunId, jobName)

    TaskResult.empty(request)
  }

  private val withJobName = (startJobRunRequest: StartJobRunRequest) =>
    startJobRunRequest
      .withJobName(jobName)

  private val withArguments = (startJobRunRequest: StartJobRunRequest) => {
    if (!arguments.isEmpty) startJobRunRequest.withArguments(arguments)
    else startJobRunRequest
  }

  private val withTimeout = (startJobRunRequest: StartJobRunRequest) =>
    timeout match {
      case Some(t) => startJobRunRequest.withTimeout(t)
      case _       => startJobRunRequest
  }

  private val withSecurityConfiguration = (startJobRunRequest: StartJobRunRequest) =>
    securityConfiguration match {
      case Some(c) => startJobRunRequest.withSecurityConfiguration(c)
      case _       => startJobRunRequest
  }

  private val withNotificationProperty = (startJobRunRequest: StartJobRunRequest) =>
    notifyDelayAfter match {
      case Some(n) =>
        startJobRunRequest
          .withNotificationProperty(new NotificationProperty().withNotifyDelayAfter(n))
      case _ => startJobRunRequest
  }

  private val withWorkerParams = (startJobRunRequest: StartJobRunRequest) =>
    workerType match {
      case Some(w) if w == "G.1X" || w == "G.2X" => startJobRunRequest.withWorkerType(w)
      case _                                     => startJobRunRequest
  }

  private val withNumberOfWorkers = (startJobRunRequest: StartJobRunRequest) =>
    numberOfWorkers match {
      case Some(n) => startJobRunRequest.withNumberOfWorkers(n)
      case _       => startJobRunRequest
  }

  private val withMaxCapacity = (startJobRunRequest: StartJobRunRequest) =>
    maxCapacity match {
      case Some(m) => startJobRunRequest.withMaxCapacity(m)
      case _       => startJobRunRequest
  }

  private val startJobRunRequestWithParams =
    withJobName
      .andThen(withArguments)
      .andThen(withWorkerParams)
      .andThen(withTimeout)
      .andThen(withNotificationProperty)
      .andThen(withSecurityConfiguration)
      .andThen(withNumberOfWorkers)
      .andThen(withMaxCapacity)
}
