package com.softwaremill.slackit.webapi.model.oauth

case class OAuthAccess(
                        access_token: String,
                        scope: String,
                        team_id: Option[String],
                        team_name: Option[String],
                        team: Option[TeamIdentity],
                        user: Option[UserIdentity],
                        bot: Option[BotUserData]
                      )
