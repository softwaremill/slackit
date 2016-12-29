package com.softwaremill.slackit.webapi.channels

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.softwaremill.slackit._
import com.softwaremill.slackit.webapi.{Endpoints, SlackEndpoint}
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

trait ChannelsApi extends SlackEndpoint {

  object channels {
    def history(token: String,
                channelId: String,
                count: Int = 100,
                inclusive: Boolean= false,
                latest: Option[String] = None,
                oldest: Option[String] = None)(implicit exec: RequestExecutor,
                                               system: ActorSystem,
                                               ec: ExecutionContext,
                                               materializer: Materializer): Source[Message, NotUsed] = {

      def params(inclusive: Boolean, latest: Option[String] = None, oldest: Option[String] = None) = {
        Map("channel" -> channelId, "count" -> count.toString) ++
          (if (inclusive) Map("inclusive" -> "1") else Map.empty) ++
          latest.map(l => Map("latest" -> l)).getOrElse(Map.empty) ++
          oldest.map(o => Map("oldest" -> o)).getOrElse(Map.empty)
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

case class ChannelObject(channel: ChannelInfo)

case class ChannelsObject(channels: List[Channel])

case class Channel(id: String,
                   name: String,
                   is_channel: Boolean,
                   created: Long,
                   creator: String,
                   is_archived: Boolean,
                   is_general: Boolean,
                   is_member: Boolean,
                   members: List[String],
                   topic: ChannelTopic,
                   purpose: ChannelPurpose,
                   previous_names: List[String],
                   num_members: Int)

case class ChannelInfo(id: String,
                       name: String,
                       is_channel: Boolean,
                       created: Long,
                       creator: String,
                       is_archived: Boolean,
                       is_general: Boolean,
                       is_starred: Boolean,
                       is_member: Boolean,
                       members: List[String],
                       topic: ChannelTopic,
                       purpose: ChannelPurpose,
                       previous_names: List[String],
                       unread_count: Int,
                       unread_count_display: Int,
                       latest: Message)

case class ChannelTopic(value: String, creator: String, last_set: Long)

case class ChannelPurpose(value: String, creator: String, last_set: Long)

case class Message(`type`: String, user: String, text: String, ts: String)

case class MessagesObject(messages: List[Message], has_more: Option[Boolean])
