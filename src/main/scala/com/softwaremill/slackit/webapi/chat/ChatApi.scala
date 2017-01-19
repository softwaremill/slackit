package com.softwaremill.slackit.webapi.chat

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.softwaremill.slackit.webapi.channels.model.{Message, SlackTimeStamp}
import com.softwaremill.slackit.{RequestExecutor, asJsonFlow, circeDecoderFlow}
import com.softwaremill.slackit.webapi.{Endpoints, SlackEndpoint}
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

trait ChatApi extends SlackEndpoint {
  import com.softwaremill.slackit.webapi.implicits._

  object chat {
    def postMessage(token: String, channel:String, message: UserMessage)(implicit exec: RequestExecutor,
                                                         system: ActorSystem,
                                                         ec: ExecutionContext,
                                                         materializer: Materializer): Source[PostMessage, NotUsed] = {
      val request = Get(Endpoints.chat.postMessage, token, message.toMap ++ Map("channel" -> channel))
      exec(request).via(asJsonFlow()).via(circeDecoderFlow[PostMessage, PostMessage](Predef.identity))
    }
  }

}

case class PostMessage(ts: SlackTimeStamp, channel: String, message: Message)

case class UserMessage(text: String) {
  def toMap: Map[String, String] = Map("text" -> text, "as_user" -> "true")
}
