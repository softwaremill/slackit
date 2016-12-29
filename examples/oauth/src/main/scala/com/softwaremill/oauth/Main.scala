package com.softwaremill.users

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.softwaremill.slackit
import com.softwaremill.slackit.webapi.model.oauth.OAuthAccess
import com.softwaremill.slackit.{Slack, SlackConfig}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App with StrictLogging {
  implicit val system       = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import scala.concurrent.ExecutionContext.Implicits.global

  val config = new SlackConfig {
    override protected def rootConfig = ConfigFactory.load()
  }

  implicit val slackConnectionFlow = Http().outgoingConnectionHttps("slack.com")

  implicit val exec = slackit.defaultRequestExecutor(slackConnectionFlow)

  val routes: Route = get {
    path("login") {
      val uri = Uri(s"https://slack.com/oauth/authorize").withQuery(
        Query(("client_id", config.clientId), ("scope", config.scopes))
      )
      redirect(uri, StatusCodes.Found)
    } ~ path("oauth") {
      parameters('code, 'state) { (code, state) =>
        val fut: Future[OAuthAccess] = Slack(config).oauth.access(code).runWith(Sink.head)
        complete(fut.map(_ => StatusCodes.OK))
      }
    }
  }

  val host = "0.0.0.0"
  val port = 8080

  Http().bindAndHandle(routes, host, port).onComplete {
    case Success(b) =>
      logger.info(s"Server started $host:$port")
      sys.addShutdownHook {
        b.unbind()
        system.terminate()
        logger.info("Server stopped")
      }
    case Failure(e) =>
      logger.error(s"Could not start server on $host:$port", e)
      sys.addShutdownHook {
        system.terminate()
        logger.info("Server stopped")
      }
  }
}
