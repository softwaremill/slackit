package com.softwaremill.slackit.webapi.users

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.testkit.SlackResponder
import com.softwaremill.slackit.webapi.Endpoints
import com.softwaremill.slackit.webapi.model.users.{Member, Profile}
import com.softwaremill.slackit.{BaseSlackSpec, Slack}
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.jawn._
import scala.util.Random

class UsersListSpec extends BaseSlackSpec {
  behavior of "Users List"

  it should "return all users data" in {
    //given
    val members = List(
      randomMember(),
      randomMember(),
      randomMember(),
      randomMember()
    )

    SlackResponder(Endpoints.users.list)
      .withPayload(successfulResponse(MembersObject(members).asJson).noSpaces)
      .commit()

    //when
    val result = Slack(config).users.list("token").runWith(Sink.seq)

    //then
    result.map(_ shouldBe members)
  }

  it should "properly behave when status is null" in {
    //given
    val memberJson =
      """
        |{
        |  "id": "U023BECGF",
        |  "team_id": "T021F9ZE2",
        |  "name": "bobby",
        |  "deleted": false,
        |  "status": null,
        |  "color": "9f69e7",
        |  "real_name": "Bobby Tables",
        |  "tz": "America\/Los_Angeles",
        |  "tz_label": "Pacific Daylight Time",
        |  "tz_offset": -25200,
        |  "profile": {
        |    "avatar_hash": "ge3b51ca72de",
        |    "first_name": "Bobby",
        |    "last_name": "Tables",
        |    "real_name": "Bobby Tables",
        |    "email": "bobby@slack.com",
        |    "skype": "my-skype-name",
        |    "phone": "+1 (123) 456 7890",
        |    "image_24": "https:\/\/...",
        |    "image_32": "https:\/\/...",
        |    "image_48": "https:\/\/...",
        |    "image_72": "https:\/\/...",
        |    "image_192": "https:\/\/..."
        |  },
        |  "is_bot": false,
        |  "is_admin": true,
        |  "is_owner": true,
        |  "has_2fa": false
        |}
      """.stripMargin
    val members = List(
        decode[Member](memberJson).right.get
    )

    SlackResponder(Endpoints.users.list)
      .withPayload(successfulResponse(MembersObject(members).asJson).noSpaces)
      .commit()

    //when
    val result = Slack(config).users.list("token").runWith(Sink.seq)

    //then
    result.map(_ shouldBe members)

  }

  def randomMember(): Member = {
    Member(randomString(),
           randomString(),
           randomString(),
           false,
           randomSomeString(),
           randomSomeString(),
           randomSomeString(),
           randomSomeString(),
           randomSomeString(),
           Some(0),
           randomProfile(),
           Some(false),
           Some(false),
           Some(false),
           Some(false),
           Some(false),
           false,
           Some(true),
           randomSomeString(),
           randomSomeString())
  }

  def randomProfile(): Profile =
    Profile(randomNoneString(),
            randomNoneString(),
            randomNoneString(),
            randomNoneString(),
            randomNoneString(),
            randomNoneString(),
            randomNoneString(),
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomString(),
            randomSomeString(),
            randomSomeString(),
            randomSomeString())

  def randomString(): String = {
    randomString(10)
  }

  def randomSomeString(): Some[String] = {
    Some(randomString())
  }

  def randomNoneString(): Option[String] = None

  def randomString(length: Int) = {
    val sb = new StringBuffer()
    val r  = new Random()

    for (i <- 1 to length) {
      sb.append((r.nextInt(25) + 65).toChar) // A - Z
    }

    sb.toString
  }
}
