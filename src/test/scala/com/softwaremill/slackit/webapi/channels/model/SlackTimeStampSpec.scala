package com.softwaremill.slackit.webapi.channels.model

import java.time.{Instant, ZoneId, ZonedDateTime}

import org.scalatest.{FlatSpec, Matchers}

class SlackTimeStampSpec extends FlatSpec with Matchers {
  behavior of "SlackTimeStamp"

  it should "decode value into dateTime and message parts" in {
    //given
    val seconds = 1483336253
    val msgId   = "006624"
    val dt      = ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault())
    val ts      = s"$seconds.$msgId"

    //when
    val result = SlackTimeStamp(ts)

    //then
    result.dateTime shouldBe dt
    result.messageId shouldBe msgId
  }

  it should "decode value into dateTime part when no message id is present" in {
    //given
    val seconds = 1483336253
    val dt      = ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault())
    val ts      = s"$seconds"

    //when
    val result = SlackTimeStamp(ts)

    //then
    result.dateTime shouldBe dt
    result.messageId shouldBe 'empty
  }
}
