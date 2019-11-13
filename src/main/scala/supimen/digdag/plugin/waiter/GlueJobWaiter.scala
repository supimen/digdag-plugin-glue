package supimen.digdag.plugin.waiter

import java.util.concurrent.{ExecutorService, Executors}

import com.amazonaws.services.glue.AWSGlue
import com.amazonaws.services.glue.model.JobRunState._
import com.amazonaws.services.glue.model.{GetJobRunRequest, GetJobRunResult}
import com.amazonaws.waiters._

class GlueJobWaiter(glue: AWSGlue, executorService: ExecutorService = Executors.newFixedThreadPool(50)) {
  def waitJobRun(runId: String, jobName: String): Unit =
    newWaiter().run(
      new WaiterParameters[GetJobRunRequest](
        new GetJobRunRequest()
          .withJobName(jobName)
          .withRunId(runId)
      )
    )

  private def newWaiter(): Waiter[GetJobRunRequest] =
    new WaiterBuilder[GetJobRunRequest, GetJobRunResult]()
      .withSdkFunction(newWaiterFunction())
      .withExecutorService(executorService)
      .withDefaultPollingStrategy(
        new PollingStrategy(
          new MaxAttemptsRetryStrategy(1000),
          new FixedDelayStrategy(30)
        )
      )
      .withAcceptors(
        newRetryAcceptor(),
        newSuccessAcceptor(),
        newFailureAcceptor()
      )
      .build()

  private def newWaiterFunction(): SdkFunction[GetJobRunRequest, GetJobRunResult] = { input: GetJobRunRequest =>
    glue.getJobRun(input)
  }

  private def newRetryAcceptor(): WaiterAcceptor[GetJobRunResult] =
    new WaiterAcceptor[GetJobRunResult] {
      override def getState: WaiterState =
        WaiterState.RETRY

      override def matches(output: GetJobRunResult): Boolean = {
        val state = output.getJobRun.getJobRunState
        state == RUNNING.toString || state == STARTING.toString || state == STOPPING.toString
      }
    }

  private def newSuccessAcceptor(): WaiterAcceptor[GetJobRunResult] =
    new WaiterAcceptor[GetJobRunResult] {
      override def getState: WaiterState =
        WaiterState.SUCCESS

      override def matches(output: GetJobRunResult): Boolean =
        output.getJobRun.getJobRunState == SUCCEEDED.toString
    }

  private def newFailureAcceptor(): WaiterAcceptor[GetJobRunResult] =
    new WaiterAcceptor[GetJobRunResult] {

      override def getState: WaiterState =
        WaiterState.FAILURE

      override def matches(output: GetJobRunResult): Boolean = {
        val state = output.getJobRun.getJobRunState
        state == FAILED.toString || state == TIMEOUT.toString || state == STOPPED.toString
      }
    }
}
