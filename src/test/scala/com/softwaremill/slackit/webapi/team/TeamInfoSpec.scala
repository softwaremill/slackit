package com.softwaremill.slackit.webapi.team

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.testkit.SlackResponder
import com.softwaremill.slackit.webapi.model.team.{Icon, Team}
import com.softwaremill.slackit.{BaseSlackSpec, Slack}
import io.circe.generic.auto._
import io.circe.syntax._

class TeamInfoSpec extends BaseSlackSpec {
  behavior of "Team Info"

  it should "return team information" in {
    //given
    val team = Team("T010101",
                    "Test team",
                    "testteam.com",
                    "",
                    Icon(
                      "",
                      "",
                      "",
                      "",
                      "",
                      "",
                      Some(true),
                      "",
                      ""
                    ))
    val teamobject = TeamObject(team)
    SlackResponder("/api/team.info").withPayload(successfulResponse(teamobject.asJson).noSpaces).commit()

    //when
    val result = Slack(config).team.info("random_token").runWith(Sink.head)

    //then
    result.map(_ shouldBe team)
  }
}
