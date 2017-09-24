package io.tzimisce.logback

import java.io.File
import java.net.URL

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.gaffer.GafferConfigurator
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.jul.LevelChangePropagator
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.bridge.SLF4JBridgeHandler
import org.slf4j.impl.StaticLoggerBinder
import org.slf4j.ILoggerFactory
import play.api.libs.logback.LogbackLoggerConfigurator
import play.api.{Environment, Mode}

class LogbackGroovyLoggerConfigurator extends LogbackLoggerConfigurator {

  def loggerFactory: ILoggerFactory = {
    StaticLoggerBinder.getSingleton.getLoggerFactory
  }


  override def configure(env: Environment): Unit = {
    val properties = Map("application.home" -> env.rootPath.getAbsolutePath)

    // Get an explicitly configured resource URL
    // Fallback to a file in the conf directory if the resource wasn't found on the classpath
    def explicitResourceUrl = sys.props.get("logger.resource").map { r =>
      env.resource(r).getOrElse(new File(env.getFile("conf"), r).toURI.toURL)
    }

    // Get an explicitly configured file URL
    def explicitFileUrl = sys.props.get("logger.file").map(new File(_).toURI.toURL)

    // Get an explicitly configured URL
    def explicitUrl = sys.props.get("logger.url").map(new URL(_))

    // application-logger.xml and logger.xml are no longer supported methods for supplying the configuration
    // Support removed in Play 2.5. This notice can be removed in future versions of Play
    if (env.resource("application-logger.xml").orElse(env.resource("logger.xml")).isDefined) {
      System.err.println("application-logger.xml and logger.xml are no longer supported. Please name your file logback.xml");
    }

    // logback.groovy
    def resourceGroovyUrl = env.resource("logback.groovy")

    // logback.xml is the documented method, logback-play-default.xml is the fallback that Play uses
    // if no other file is found
    def resourceUrl = env.resource("logback.xml")
      .orElse(env.resource(
        if (env.mode == Mode.Dev) "logback-play-dev.xml" else "logback-play-default.xml"
      ))

    val configUrl = explicitResourceUrl orElse explicitFileUrl orElse explicitUrl orElse resourceGroovyUrl orElse resourceUrl

    configure(properties, configUrl)
  }


  override def configure(properties: Map[String, String], config: Option[URL]): Unit = {

    loggerFactory.synchronized {
      // Redirect JUL -> SL4FJ

      // Remove existing handlers from JUL
      SLF4JBridgeHandler.removeHandlersForRootLogger()

      val ctx = loggerFactory.asInstanceOf[LoggerContext]

      config match {
        case Some(url) =>
          url match {
            case msg if msg.getPath.endsWith(".groovy") =>
              val configurator = new GafferConfigurator(ctx)
              configureLogger(ctx)
              properties.foreach { case (k, v) => ctx.putProperty(k, v) }
              configurator.run(url)
            case _ =>
              val configurator = new JoranConfigurator
              configurator.setContext(ctx)
              configureLogger(ctx)
              properties.foreach { case (k, v) => ctx.putProperty(k, v) }
              configurator.doConfigure(url)
          }
        case None =>
          System.err.println("Could not detect a logback configuration file, not configuring logback")
      }
      StatusPrinter.printIfErrorsOccured(ctx)
    }
  }

  private def configureLogger(loggerContext: LoggerContext): Unit = {

    val levelChangePropagator = new LevelChangePropagator()
    levelChangePropagator.setContext(loggerContext)
    levelChangePropagator.setResetJUL(true)
    loggerContext.addListener(levelChangePropagator)
    SLF4JBridgeHandler.install()

    loggerContext.reset()

    // Ensure that play.Logger and play.api.Logger are ignored when detecting file name and line number for
    // logging
    val frameworkPackages = loggerContext.getFrameworkPackages
    frameworkPackages.add(classOf[play.Logger].getName)
    frameworkPackages.add(classOf[play.api.Logger].getName)

  }

}
