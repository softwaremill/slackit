package com.softwaremill.slackit.webapi.channels.model

import io.circe.jawn._
import org.scalatest.{FlatSpec, Matchers}

class MessageDecoderSpec extends FlatSpec with Matchers {
  behavior of "Message Decoder"

  import implicits._

  it should "properly decode MeMessage" in {
    //given
    val json =
      """
        |{
        |  "text": "example text",
        |  "type": "message",
        |  "subtype": "me_message",
        |  "user": "U00000000",
        |  "ts": "1483341542.006633"
        |}
      """.stripMargin

    //when
    val msg = decode[Message](json)

    //then
    msg shouldBe Right(
      SubtypedMessage("U00000000", "example text", SlackTimeStamp("1483341542.006633"), "me_message", None))
  }

  it should "properly decode JoinMessage" in {
    //given
    val json =
      """
        |{
        |  "user": "U00000000",
        |  "text": "<@U00000000|user> has joined the channel",
        |  "type": "message",
        |  "subtype": "channel_join",
        |  "ts": "1483339158.006626"
        |}
      """.stripMargin

    //when
    val msg = decode[Message](json)

    //then
    msg shouldBe Right(
      SubtypedMessage("U00000000",
                      "<@U00000000|user> has joined the channel",
                      SlackTimeStamp("1483339158.006626"),
                      "channel_join",
                      None))
  }
}
