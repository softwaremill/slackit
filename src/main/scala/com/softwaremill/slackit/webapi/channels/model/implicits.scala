package com.softwaremill.slackit.webapi.channels.model

import io.circe.generic.auto._
import io.circe.{Decoder, HCursor}

object implicits {
  implicit val slackTimeStampDecoder: Decoder[SlackTimeStamp] = Decoder.decodeString.map(SlackTimeStamp(_))

  implicit val messageDecoder: Decoder[Message] = new Decoder[Message] {
    override def apply(c: HCursor) = {
      c.get[String]("subtype") match {
        case Right("bot_message") => c.as[BotMessage]
        case Right(_)             => c.as[SubtypedMessage]
        case _                    => c.as[RegularMessage]
      }
    }
  }
}
