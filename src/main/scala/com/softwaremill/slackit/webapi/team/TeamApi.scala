package com.softwaremill.slackit.webapi.team

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.softwaremill.slackit.RequestExecutor
import com.softwaremill.slackit.webapi.model.team.Team
import com.softwaremill.slackit.webapi.{Endpoints, SlackEndpoint}
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

trait TeamApi extends SlackEndpoint {
  import de.heikoseeberger.akkahttpcirce.CirceSupport._

  object team {
    def info(token: String)(implicit exec: RequestExecutor,
                            system: ActorSystem,
                            materializer: Materializer,
                            ec: ExecutionContext): Source[Team, NotUsed] = {
      exec(Get(Endpoints.team.info, token)).mapAsync(1)(r => Unmarshal(r.entity).to[TeamObject].map(_.team))
    }
  }
}

case class TeamObject(team: Team)
