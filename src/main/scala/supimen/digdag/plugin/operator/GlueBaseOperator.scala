package supimen.digdag.plugin.operator

import com.amazonaws.auth.profile.{ProfileCredentialsProvider, ProfilesConfigFile}
import com.amazonaws.auth.{
  AWSCredentials,
  AWSCredentialsProvider,
  AWSStaticCredentialsProvider,
  AnonymousAWSCredentials,
  BasicAWSCredentials,
  BasicSessionCredentials,
  EC2ContainerCredentialsProviderWrapper,
  EnvironmentVariableCredentialsProvider,
  SystemPropertiesCredentialsProvider,
  WebIdentityTokenCredentialsProvider
}
import com.amazonaws.regions.Regions
import com.amazonaws.services.glue.{AWSGlue, AWSGlueClientBuilder}
import com.typesafe.scalalogging.LazyLogging
import io.digdag.client.config.ConfigException
import io.digdag.spi.OperatorContext
import io.digdag.util.BaseOperator

abstract class GlueBaseOperator(context: OperatorContext) extends BaseOperator(context) with LazyLogging {
  protected val params = request.getConfig
  private val region   = Option(params.getOptional("region", classOf[String]).orNull())

  def glueClient(context: OperatorContext): AWSGlue = {
    val authMethod = params.getOptional("auth_method", classOf[String]).or("basic")
    val credentialsProvider: AWSCredentialsProvider = authMethod match {
      case "basic"      => basicAuthMethodAWSCredentialsProvider
      case "env"        => new EnvironmentVariableCredentialsProvider
      case "instance"   => new EC2ContainerCredentialsProviderWrapper
      case "profile"    => profileAuthMethodAWSCredentialsProvider
      case "properties" => new SystemPropertiesCredentialsProvider
      case _ =>
        throw new ConfigException(
          s"""auth_method: "$authMethod" is not supported. available `auth_method`s are "basic", "env", "instance", "profile", or "properties"."""
        )
    }

    AWSGlueClientBuilder
      .standard()
      .withCredentials(
        credentialsProvider
      )
      .withRegion {
        region match {
          case Some(r) => Regions.fromName(r)
          case _       => Regions.DEFAULT_REGION
        }
      }
      .build()
  }

  private def basicAuthMethodAWSCredentialsProvider: AWSCredentialsProvider = {
    val secrets         = context.getSecrets.getSecrets("glue")
    val accessKeyId     = Option(secrets.getSecretOptional("access_key_id").orNull())
    val secretAccessKey = Option(secrets.getSecretOptional("secret_access_key_id").orNull())
    if (accessKeyId.isEmpty)
      throw new ConfigException(s"""`access_key_id` must be set when `auth_method` is basic.""")
    if (secretAccessKey.isEmpty)
      throw new ConfigException(s"""`secret_access_key_id` must be set when `auth_method` is basic.""")
    new AWSStaticCredentialsProvider(
      new BasicAWSCredentials(accessKeyId.get, secretAccessKey.get)
    )
  }

  private def profileAuthMethodAWSCredentialsProvider: AWSCredentialsProvider = {
    val profileFile = Option(params.getOptional("profile_file", classOf[String]).orNull())
    val profileName = params.getOptional("profile_name", classOf[String]).or("default")
    if (profileFile.isEmpty) return new ProfileCredentialsProvider(profileName)
    val pf = profileFile match {
      case Some(a) => new ProfilesConfigFile(a)
      case None    => new ProfilesConfigFile()
    }
    new ProfileCredentialsProvider(pf, profileName)
  }
}
