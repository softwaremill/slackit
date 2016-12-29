package com.softwaremill.slackit.webapi.oauth

import akka.stream.scaladsl.Sink
import com.softwaremill.slackit.testkit.SlackResponder
import com.softwaremill.slackit.webapi.Errors.{BadClientSecret, InvalidClientId}
import com.softwaremill.slackit.webapi.model.oauth.OAuthAccess
import com.softwaremill.slackit.{BaseSlackSpec, Slack}
import io.circe.generic.auto._
import io.circe.syntax._

class SlackOauthAccessSpec extends BaseSlackSpec {
  behavior of "Slack oauth.access"

  it should "return error when oauth.access responds with missing clientId error" in {
    //given
    val expectedError = "invalid_client_id"
    val oauthJson     = errorResponse(expectedError)
    SlackResponder("/api/oauth.access").withPayload(oauthJson.noSpaces).commit()

    //when
    val result = Slack(config).oauth.access("").runWith(Sink.head)

    //then
    recoverToSucceededIf[InvalidClientId](result)
  }

  it should "return error when oauth.access responds with missing clientSecret error" in {
    //given
    val expectedError = "bad_client_secret"
    val oauthJson     = errorResponse(expectedError)
    SlackResponder("/api/oauth.access").withPayload(oauthJson.noSpaces).commit()

    //when
    val result = Slack(config).oauth.access("").runWith(Sink.head)

    //then
    recoverToSucceededIf[BadClientSecret](result)
  }

  it should "return OAuth basic data" in {
    //given
    val expectedOauth = OAuthAccess("access_token", "scope", Some("team_id"), Some("team_name"), None, None, None)
    val oauthJson     = successfulResponse(expectedOauth.asJson)
    SlackResponder("/api/oauth.access").withPayload(oauthJson.noSpaces).commit()

    //when
    val result = Slack(config).oauth.access("some_valid_code").runWith(Sink.head)

    //then
    result.map {
      _ shouldBe expectedOauth
    }
  }
}
