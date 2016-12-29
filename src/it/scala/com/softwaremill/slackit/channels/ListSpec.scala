package com.softwaremill.slackit.channels

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.{BaseItSpec, Slack}

class ListSpec extends BaseItSpec {
  behavior of "channels.list"

  it should "return a list of all channels (including archived)" in {
    //given
    val token           = config.token
    val excludeArchived = false

    //when
    val result = Slack(config).channels.list(token, excludeArchived).filter(_.is_archived).runWith(Sink.seq)

    //then
    result.map(_ shouldNot be('empty))
  }

  it should "return a list of channels excluding archived" in {
    //given
    val token           = config.token
    val excludeArchived = true

    //when
    val result = Slack(config).channels.list(token, excludeArchived).filter(_.is_archived).runWith(Sink.seq)

    //then
    result.map(_ shouldBe 'empty)
  }
}
