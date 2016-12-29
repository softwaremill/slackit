package com.softwaremill.slackit.webapi.users

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.softwaremill.slackit.webapi.model.oauth.OAuthAccess
import com.softwaremill.slackit.webapi.model.users.{Member, Presence}
import com.softwaremill.slackit.webapi.{Endpoints, SlackEndpoint}
import com.softwaremill.slackit.{RequestExecutor, asJsonFlow, circeDecoderFlow}
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

trait UsersApi extends SlackEndpoint {

  object users {

    private def GetUsersInfo(token: String, userId: String) = Get(Endpoints.users.info, token, Map("user" -> userId))

    private def GetUsersList(token: String) = Get(Endpoints.users.list, token)

    private def GetUsersGetPresence(token: String, userId: String) =
      Get(Endpoints.users.getPresence, token, Map("user" -> userId))

    private def GetUsersIdentity(token: String) = Get(Endpoints.users.identity, token)

    def getPresence(token: String, userId: String)(implicit exec: RequestExecutor,
                                                   system: ActorSystem,
                                                   ec: ExecutionContext,
                                                   materializer: Materializer): Source[Presence, NotUsed] = {
      exec(GetUsersGetPresence(token, userId))
        .via(asJsonFlow())
        .via(circeDecoderFlow[Presence, Presence](Predef.identity))
    }

    def identity(token: String)(implicit exec: RequestExecutor,
                                system: ActorSystem,
                                ec: ExecutionContext,
                                materializer: Materializer): Source[OAuthAccess, NotUsed] = {
      exec(GetUsersIdentity(token)).via(asJsonFlow()).via(circeDecoderFlow[OAuthAccess, OAuthAccess](Predef.identity))
    }

    def info(token: String, userId: String)(implicit exec: RequestExecutor,
                                            system: ActorSystem,
                                            ec: ExecutionContext,
                                            materializer: Materializer): Source[Member, NotUsed] = {
      exec(GetUsersInfo(token, userId)).via(asJsonFlow()).via(circeDecoderFlow[UserObject, Member](_.user))
    }

    def list(token: String, presence: Boolean = true)(implicit exec: RequestExecutor,
                                                      system: ActorSystem,
                                                      ec: ExecutionContext,
                                                      materializer: Materializer): Source[Member, NotUsed] = {
      exec(GetUsersList(token))
        .via(asJsonFlow())
        .via(circeDecoderFlow[MembersObject, List[Member]](_.members))
        .mapConcat(Predef.identity)
    }
  }

}

case class MembersObject(members: List[Member])

case class UserObject(user: Member)
