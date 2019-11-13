package supimen.digdag.plugin.operator

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.Regions
import com.amazonaws.services.glue.{AWSGlue, AWSGlueClientBuilder}
import com.typesafe.scalalogging.LazyLogging
import io.digdag.spi.OperatorContext
import io.digdag.util.BaseOperator

abstract class GlueBaseOperator(context: OperatorContext) extends BaseOperator(context) with LazyLogging {
  protected val params = request.getConfig
  private val region   = Option(params.getOptional("region", classOf[String]).orNull())

  def glueClient(context: OperatorContext): AWSGlue = {
    val secrets         = context.getSecrets.getSecrets("glue")
    val accessKeyId     = secrets.getSecretOptional("access_key_id").get()
    val secretAccessKey = secrets.getSecretOptional("secret_access_key_id").get()
    AWSGlueClientBuilder
      .standard()
      .withCredentials(
        new AWSStaticCredentialsProvider(
          new BasicAWSCredentials(accessKeyId, secretAccessKey)
        )
      )
      .withRegion {
        region match {
          case Some(r) => Regions.fromName(r)
          case _       => Regions.DEFAULT_REGION
        }
      }
      .build()
  }
}
