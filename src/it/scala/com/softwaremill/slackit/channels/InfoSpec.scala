package com.softwaremill.slackit.channels

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.{BaseItSpec, Slack}

class InfoSpec extends BaseItSpec {
behavior of "channels.info"

  it should "return info about a channel" in {
    //given
    val token = config.token
    val channelId = config.testChannelId

    //when
    val result = Slack(config).channels.info(token, channelId).runWith(Sink.head)

    //then
    result.map(_.id shouldBe channelId)
  }
}
