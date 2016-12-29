package com.softwaremill.slackit.users

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.{BaseItSpec, Slack}

class GetPresenceSpec extends BaseItSpec {
  behavior of "users.getPresence"

  it should "return presence data" in {
    //given
    val userId = config.testUserId

    //when
    val stream = Slack(config).users.getPresence(config.token, userId).runWith(Sink.head)

    //then
    stream.map(_.presence shouldBe "active")
  }
}
