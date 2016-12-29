package com.softwaremill.users

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.softwaremill.slackit
import com.softwaremill.slackit.{Slack, SlackConfig}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging

object Main extends App with StrictLogging {
  implicit val system       = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import scala.concurrent.ExecutionContext.Implicits.global

  val config = new SlackConfig with AppConfig {
    override protected def rootConfig = ConfigFactory.load()
  }

  implicit val slackConnectionFlow = Http().outgoingConnectionHttps("slack.com")

  implicit val exec = slackit.defaultRequestExecutor(slackConnectionFlow)

  Slack(config).users.list(config.testToken).runForeach(println).onComplete(_ => system.terminate())
}

trait AppConfig {
  protected def rootConfig: Config

  lazy val testToken = rootConfig.getString("app.token")
}
