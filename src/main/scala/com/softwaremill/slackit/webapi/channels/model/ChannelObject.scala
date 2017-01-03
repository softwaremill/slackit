package com.softwaremill.slackit.webapi.channels.model

case class ChannelObject(channel: ChannelInfo)

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
