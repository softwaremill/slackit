package com.softwaremill.slackit.channels

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.{BaseItSpec, Slack}

class HistorySpec extends BaseItSpec {
  behavior of "channels.history"

  it should "load last 100 messages" in {
    //given
    val token   = config.token
    val channel = config.testChannelId
    val count   = 100

    //when
    val result = Slack(config).channels.history(token, channel, count).take(count).runWith(Sink.seq)

    //then
    result.map(_ should have size count)
  }

  it should "load messages between latest and oldest (not inclusive)" in {
    //given
    val token   = config.token
    val channel = config.testChannelId
    val count   = 100
    val latest  = Some("1483013245.006118")
    val oldest  = Some("1483013189.006114")

    //when
    val result = Slack(config).channels.history(token, channel, count, false, latest, oldest).runWith(Sink.seq)

    //then
    result.map(_ should have size 3)
  }

  it should "load messages between latest and oldest (inclusive)" in {
    //given
    val token   = config.token
    val channel = config.testChannelId
    val count   = 100
    val latest  = Some("1483013245.006118")
    val oldest  = Some("1483013189.006114")

    //when
    val result = Slack(config).channels.history(token, channel, count, true, latest, oldest).runWith(Sink.seq)

    //then
    result.map(_ should have size 5)
  }
}
