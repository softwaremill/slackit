package com.softwaremill.slackit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.softwaremill.slackit
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext

trait BaseItSpec extends AsyncFlatSpec with Matchers {
  implicit val system                = ActorSystem()
  implicit val materializer          = ActorMaterializer()
  implicit val ec: ExecutionContext  = ExecutionContext.Implicits.global
  implicit val slackConnectionFlow   = Http().outgoingConnectionHttps("slack.com")
  implicit val exec: RequestExecutor = slackit.defaultRequestExecutor(slackConnectionFlow)

  val config = new SlackConfig with ItConfig {
    override protected def rootConfig = ConfigFactory.load()
  }

  trait ItConfig {
    protected def rootConfig: Config

    val token = rootConfig.getString("it.token")
    val testUserId = rootConfig.getString("it.user")
    val testChannelId = rootConfig.getString("it.channelId")
  }
}
