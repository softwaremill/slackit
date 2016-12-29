package com.softwaremill.slackit

import com.typesafe.config.Config

trait SlackConfig {
  protected def rootConfig: Config

  lazy val clientId: String     = rootConfig.getString("slack.clientId")
  lazy val clientSecret: String = rootConfig.getString("slack.clientSecret")
  lazy val scopes: String       = rootConfig.getString("slack.scopes")
}
