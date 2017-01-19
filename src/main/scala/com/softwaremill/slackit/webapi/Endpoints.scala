package com.softwaremill.slackit.webapi

object Endpoints {

  object channels {
    val history = "/api/channels.history"
    val info    = "/api/channels.info"
    val list    = "/api/channels.list"
  }

  object chat {
    val postMessage = "/api/chat.postMessage"
  }

  object oauth {
    val access = "/api/oauth.access"
  }

  object team {
    val info = "/api/team.info"
  }

  object users {
    val getPresence = "/api/users.getPresence"
    val identity    = "/api/users.identity"
    val info        = "/api/users.info"
    val list        = "/api/users.list"
  }

}
