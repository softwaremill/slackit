package com.softwaremill.slackit.webapi.channels

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.softwaremill.slackit._
import com.softwaremill.slackit.webapi.channels.model._
import com.softwaremill.slackit.webapi.{Endpoints, SlackEndpoint}
import io.circe.generic.auto._
import com.softwaremill.slackit.webapi.implicits._

import scala.concurrent.{ExecutionContext, Future}

trait ChannelsApi extends SlackEndpoint {

  object channels {

    def history(token: String,
                channelId: String,
                count: Int = 100,
                inclusive: Boolean = false,
                latest: Option[SlackTimeStamp] = None,
                oldest: Option[SlackTimeStamp] = None)(implicit exec: RequestExecutor,
                                                       system: ActorSystem,
                                                       ec: ExecutionContext,
                                                       materializer: Materializer): Source[Message, NotUsed] = {

      def params(inclusive: Boolean, latest: Option[SlackTimeStamp] = None, oldest: Option[SlackTimeStamp] = None) = {
        Map("channel" -> channelId, "count" -> count.toString) ++
          (if (inclusive) Map("inclusive" -> "1") else Map.empty) ++
          latest.map(l => Map("latest" -> l.toString)).getOrElse(Map.empty) ++
          oldest.map(o => Map("oldest" -> o.toString)).getOrElse(Map.empty)
      }

      def loadNext(mo: MessagesObject): Option[(Option[HttpRequest], List[Message])] =
        mo.has_more.flatMap(hm => {
          if (hm) {
            Some(Some(
                   Get(Endpoints.channels.history,
                       token,
                       params(inclusive, latest = mo.messages.lastOption.map(_.ts), oldest))),
                 mo.messages)
          } else {
            Some(None, mo.messages)
          }
        })

      Source
        .unfoldAsync(Option(Get(Endpoints.channels.history, token, params(inclusive, latest, oldest)))) { request =>
          if (request.isDefined) {
            exec(request.get)
              .via(asJsonFlow())
              .via(circeDecoderFlow[MessagesObject, MessagesObject](identity))
              .runWith(Sink.head)
              .map(loadNext)
          } else {
            Future.successful(None)
          }

        }
        .mapConcat(identity)
    }

    def info(token: String, channelId: String)(implicit exec: RequestExecutor,
                                               system: ActorSystem,
                                               ec: ExecutionContext,
                                               materializer: Materializer): Source[ChannelInfo, NotUsed] = {
      exec(Get(Endpoints.channels.info, token, Map("channel" -> channelId)))
        .via(asJsonFlow())
        .via(circeDecoderFlow[ChannelObject, ChannelInfo](_.channel))
    }

    def list(token: String, excludeArchived: Boolean = false)(implicit exec: RequestExecutor,
                                                              system: ActorSystem,
                                                              ec: ExecutionContext,
                                                              materializer: Materializer): Source[Channel, NotUsed] = {
      val archived: Map[String, String] = if (excludeArchived) Map("exclude_archived" -> "1") else Map.empty

      exec(Get(Endpoints.channels.list, token, archived))
        .via(asJsonFlow())
        .via(circeDecoderFlow[ChannelsObject, List[Channel]](_.channels))
        .mapConcat(identity)
    }
  }

}
