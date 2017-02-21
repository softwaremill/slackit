package com.softwaremill.slackit.webapi

import com.softwaremill.slackit.webapi.channels.model._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor}

object implicits {
  implicit val slackTimeStampDecoder: Decoder[SlackTimeStamp] = Decoder.decodeString.map(SlackTimeStamp(_))

  implicit val messageDecoder: Decoder[Message] = new Decoder[Message] {
    override def apply(c: HCursor) = {
      c.get[String]("subtype") match {
        case Right("bot_message")  => c.as[BotMessage]
        case Right("file_comment") => c.as[FileCommentMessage]
        case Right(_)              => c.as[SubtypedMessage]
        case _ =>
          c.get[String]("parent_user_id") match {
            case Right(_) => c.as[ThreadReply]
            case _ =>
              c.get[Int]("reply_count") match {
                case Right(_) => c.as[MessageWithThread]
                case _        => c.as[RegularMessage]
              }
          }
      }
    }
  }

  implicit val slackTimeStampEncoder: Encoder[SlackTimeStamp] = new Encoder[SlackTimeStamp] {
    override def apply(a: SlackTimeStamp) = a.toString.asJson
  }

  implicit val messageEncoder: Encoder[Message] = new Encoder[Message] {
    override def apply(a: Message) = {
      a match {
        case msg: RegularMessage    => msg.asJson
        case msg: MessageWithThread => msg.asJson
        case msg: SubtypedMessage   => msg.asJson
        case msg: BotMessage        => msg.asJson
        case msg: ThreadReply       => msg.asJson
        case _                      => throw new Error(s"Could not encode message $a")
      }
    }
  }
}
