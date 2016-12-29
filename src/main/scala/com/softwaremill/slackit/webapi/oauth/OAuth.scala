package com.softwaremill.slackit.webapi.oauth

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.softwaremill.slackit
import com.softwaremill.slackit.webapi.model.oauth.OAuthAccess
import com.softwaremill.slackit.webapi.{Endpoints, SlackEndpoint}
import com.softwaremill.slackit.{RequestExecutor, asJsonFlow}
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

trait OAuth extends SlackEndpoint {
  object oauth {
    def access(code: String)(implicit exec: RequestExecutor,
                             system: ActorSystem,
                             ec: ExecutionContext,
                             materializer: Materializer): Source[OAuthAccess, NotUsed] = {
      val uri = Uri(Endpoints.oauth.access).withQuery(
        Query(
          ("client_id", config.clientId),
          ("client_secret", config.clientSecret),
          ("code", code),
          ("redirect_uri", "")
        ))
      val request = RequestBuilding.Get(uri)
      exec(request).via(asJsonFlow()).via(slackit.circeDecoderFlow[OAuthAccess, OAuthAccess](identity))
    }
  }
}
