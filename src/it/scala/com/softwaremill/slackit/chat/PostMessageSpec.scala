package com.softwaremill.slackit.chat

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.webapi.channels.model.RegularMessage
import com.softwaremill.slackit.webapi.chat.UserMessage
import com.softwaremill.slackit.{BaseItSpec, Slack}
import org.scalatest.Assertions

class PostMessageSpec extends BaseItSpec {
  behavior of "chat.postMessage"

  it should "post a message as user in specified channel" in {
    //given
    val token   = config.token
    val channel = config.testChannelId
    val text    = "yupi ka yey"
    val message = UserMessage(text)

    //when
    val result = Slack(config).chat.postMessage(token, channel, message).runWith(Sink.head)

    //then
    result.map(msg =>
      msg.message match {
        case RegularMessage(_, txt, _, _) => txt shouldBe text
        case _                            => Assertions.fail("Should not happen")
    })
  }
}
