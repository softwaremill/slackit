package com.softwaremill.slackit.users

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.{BaseItSpec, Slack}

class ListSpec extends BaseItSpec {
  behavior of "users.list"

  it should "retrieve a list of all team members based on provided token" in {
    //given
    val token = config.token

    //when
    val result = Slack(config).users.list(token).runWith(Sink.head)

    //then
    result map (r => r.id shouldNot be('empty))
  }
}
