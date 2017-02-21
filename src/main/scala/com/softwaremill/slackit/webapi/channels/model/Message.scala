package com.softwaremill.slackit.webapi.channels.model

sealed trait Message {
  def ts: SlackTimeStamp
}

case class RegularMessage(user: String, text: String, ts: SlackTimeStamp, reactions: Option[List[Reaction]])
    extends Message

case class MessageWithThread(user: String,
                             text: String,
                             ts: SlackTimeStamp,
                             reactions: Option[List[Reaction]],
                             thread_ts: SlackTimeStamp,
                             reply_count: Int,
                             replies: List[ThreadMapping],
                             subscribed: Boolean,
                             unread_count: Int)
    extends Message

case class ThreadMapping(user: String, ts: SlackTimeStamp)

case class SubtypedMessage(user: String,
                           text: String,
                           ts: SlackTimeStamp,
                           subtype: String,
                           reactions: Option[List[Reaction]])
    extends Message

case class BotMessage(bot_id: String, ts: SlackTimeStamp, reactions: Option[List[Reaction]]) extends Message

case class FileCommentMessage(text: String, ts: SlackTimeStamp, reactions: Option[List[Reaction]]) extends Message

case class ThreadReply(user: String,
                       text: String,
                       thread_ts: SlackTimeStamp,
                       parent_user_id: String,
                       ts: SlackTimeStamp)
    extends Message

case class MessagesObject(messages: List[Message], has_more: Option[Boolean])
