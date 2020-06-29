name := "digdag-plugin-glue"
organization := "supimen.digdag.plugin"
organizationName := "supimen"
version := "0.1.1"
scalaVersion := "2.13.1"
resolvers ++= Seq(Resolver.bintrayRepo("digdag", "maven"))
val digdagVersion = "0.9.39"

val digdagDependencies = Seq(
  "io.digdag"                       % "digdag-spi"                     % digdagVersion % Provided,
  "io.digdag"                       % "digdag-plugin-utils"            % digdagVersion % Provided,
  "commons-io"                      % "commons-io"                     % "2.5"         % Provided,
  "org.jboss.spec.javax.ws.rs"      % "jboss-jaxrs-api_2.0_spec"       % "1.0.0.Final" % Provided,
  "junit"                           % "junit"                          % "4.12"        % Provided,
  "javax.activation"                % "activation"                     % "1.1.1"       % Provided,
  "org.jboss.spec.javax.annotation" % "jboss-annotations-api_1.2_spec" % "1.0.0.Final" % Provided,
  "org.jboss.logging"               % "jboss-logging-annotations"      % "2.1.0.Final" % Provided,
  "org.jboss.logging"               % "jboss-logging-processor"        % "2.1.0.Final" % Provided,
  "org.jboss.spec.javax.servlet"    % "jboss-servlet-api_3.1_spec"     % "1.0.0.Final" % Provided,
  "net.jcip"                        % "jcip-annotations"               % "1.0"         % Provided,
  "org.apache.httpcomponents"       % "httpclient"                     % "4.5.2"       % Provided
)

libraryDependencies ++= digdagDependencies ++ Seq(
  "com.amazonaws"              % "aws-java-sdk"   % "1.11.653",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)
