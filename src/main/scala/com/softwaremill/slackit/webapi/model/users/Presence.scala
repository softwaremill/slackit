package com.softwaremill.slackit.webapi.model.users

case class Presence(presence: String,
                    online: Boolean,
                    auto_away: Boolean,
                    manual_away: Boolean,
                    connection_count: Int,
                    last_activity: Long)
