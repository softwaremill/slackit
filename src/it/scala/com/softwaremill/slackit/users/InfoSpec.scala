package com.softwaremill.slackit.users

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.{BaseItSpec, Slack}

class InfoSpec extends BaseItSpec {
  behavior of "users.info"

  it should "return user's info" in {
    //given
    val token  = config.token
    val userId = config.testUserId

    //when
    val result = Slack(config).users.info(token, userId).runWith(Sink.head)

    //then
    result.map(_.id shouldNot be('empty))
  }
}
