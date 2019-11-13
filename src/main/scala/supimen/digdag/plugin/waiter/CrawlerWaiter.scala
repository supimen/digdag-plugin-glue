package supimen.digdag.plugin.waiter

import java.util.concurrent.{ExecutorService, Executors}

import com.amazonaws.services.glue.AWSGlue
import com.amazonaws.services.glue.model.CrawlerState.{READY, RUNNING, STOPPING}
import com.amazonaws.services.glue.model.{GetCrawlerRequest, GetCrawlerResult}
import com.amazonaws.waiters._

class CrawlerWaiter(glue: AWSGlue, executorService: ExecutorService = Executors.newFixedThreadPool(50)) {
  def waitCrawler(crawlerName: String): Unit =
    newWaiter().run(
      new WaiterParameters[GetCrawlerRequest](
        new GetCrawlerRequest()
          .withName(crawlerName)
      )
    )

  private def newWaiter(): Waiter[GetCrawlerRequest] =
    new WaiterBuilder[GetCrawlerRequest, GetCrawlerResult]()
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
        newSuccessAcceptor()
      )
      .build()

  private def newWaiterFunction(): SdkFunction[GetCrawlerRequest, GetCrawlerResult] = { input: GetCrawlerRequest =>
    glue.getCrawler(input)
  }

  private def newRetryAcceptor(): WaiterAcceptor[GetCrawlerResult] =
    new WaiterAcceptor[GetCrawlerResult] {
      override def getState: WaiterState =
        WaiterState.RETRY

      override def matches(output: GetCrawlerResult): Boolean = {
        val state = output.getCrawler.getState
        state == RUNNING.toString || state == STOPPING.toString
      }
    }

  private def newSuccessAcceptor(): WaiterAcceptor[GetCrawlerResult] =
    new WaiterAcceptor[GetCrawlerResult] {
      override def getState: WaiterState =
        WaiterState.SUCCESS

      override def matches(output: GetCrawlerResult): Boolean =
        output.getCrawler.getState == READY.toString
    }
}
