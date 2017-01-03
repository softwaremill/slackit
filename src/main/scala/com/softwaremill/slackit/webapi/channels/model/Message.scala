package com.softwaremill.slackit.webapi.channels.model

sealed trait Message {
  def ts: SlackTimeStamp
  def reactions: Option[List[Reaction]]
}

sealed trait UserMessage {
  def user: String
  def text: String
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

case class MessagesObject(messages: List[Message], has_more: Option[Boolean])
