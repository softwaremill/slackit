package com.softwaremill.slackit.webapi.channels.model

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

case class ChannelPurpose(value: String, creator: String, last_set: Long)

case class ChannelTopic(value: String, creator: String, last_set: Long)
