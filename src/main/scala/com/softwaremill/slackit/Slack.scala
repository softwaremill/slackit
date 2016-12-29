package com.softwaremill.slackit

import akka.stream.Materializer
import com.softwaremill.slackit.webapi.channels.ChannelsApi
import com.softwaremill.slackit.webapi.oauth.OAuth
import com.softwaremill.slackit.webapi.team.TeamApi
import com.softwaremill.slackit.webapi.users.UsersApi

import scala.concurrent.ExecutionContext

case class Slack(config: SlackConfig)(implicit exec: RequestExecutor, ec: ExecutionContext, materializer: Materializer)
    extends OAuth
    with TeamApi
    with UsersApi
    with ChannelsApi
