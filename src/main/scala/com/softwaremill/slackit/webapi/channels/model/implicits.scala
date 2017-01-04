package com.softwaremill.slackit.webapi.channels.model

import io.circe.generic.auto._
import io.circe.{Decoder, Encoder, HCursor}
import io.circe.syntax._

object implicits {
  implicit val slackTimeStampDecoder: Decoder[SlackTimeStamp] = Decoder.decodeString.map(SlackTimeStamp(_))

  implicit val messageDecoder: Decoder[Message] = new Decoder[Message] {
    override def apply(c: HCursor) = {
      c.get[String]("subtype") match {
        case Right("bot_message")  => c.as[BotMessage]
        case Right("file_comment") => c.as[FileCommentMessage]
        case Right(_)              => c.as[SubtypedMessage]
        case _                     => c.as[RegularMessage]
      }
    }
  }

  implicit val slackTimeStampEncoder: Encoder[SlackTimeStamp] = new Encoder[SlackTimeStamp] {
    override def apply(a: SlackTimeStamp) = a.toString.asJson
  }

  implicit val messageEncoder: Encoder[Message] = new Encoder[Message] {
    override def apply(a: Message) = {
      a match {
        case msg: RegularMessage  => msg.asJson
        case msg: SubtypedMessage => msg.asJson
        case msg: BotMessage      => msg.asJson
        case _                    => throw new Error(s"Could not encode message $a")
      }
    }
  }
}
