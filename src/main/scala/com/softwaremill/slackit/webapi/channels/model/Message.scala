package com.softwaremill.slackit.webapi.channels.model

sealed trait Message {
  def ts: SlackTimeStamp
  def reactions: Option[List[Reaction]]
}

sealed trait TextMessage {
  def text: String
}

sealed trait UserMessage extends TextMessage {
  def user: String
}

case class RegularMessage(user: String, text: String, ts: SlackTimeStamp, reactions: Option[List[Reaction]])
    extends UserMessage
    with Message

case class SubtypedMessage(user: String,
                           text: String,
                           ts: SlackTimeStamp,
                           subtype: String,
                           reactions: Option[List[Reaction]])
    extends UserMessage
    with Message

case class BotMessage(bot_id: String, ts: SlackTimeStamp, reactions: Option[List[Reaction]]) extends Message

case class FileCommentMessage(text: String, ts: SlackTimeStamp, reactions: Option[List[Reaction]])
    extends Message
    with TextMessage

case class MessagesObject(messages: List[Message], has_more: Option[Boolean])
