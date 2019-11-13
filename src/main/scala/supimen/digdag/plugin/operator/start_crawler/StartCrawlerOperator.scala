package supimen.digdag.plugin.operator.start_crawler

import com.amazonaws.services.glue.model.StartCrawlerRequest
import io.digdag.spi.{OperatorContext, TaskResult}
import supimen.digdag.plugin.operator.GlueBaseOperator
import supimen.digdag.plugin.waiter.CrawlerWaiter

class StartCrawlerOperator(context: OperatorContext) extends GlueBaseOperator(context) {
  private val crawlerName = params.get("crawler_name", classOf[String])

  override def runTask(): TaskResult = {
    val glue = glueClient(context)
    glue.startCrawler(new StartCrawlerRequest().withName(crawlerName))

    logger.info(s"Running CrawlerName:$crawlerName.")

    new CrawlerWaiter(glueClient(context)).waitCrawler(crawlerName)

    TaskResult.empty(request)
  }
}
