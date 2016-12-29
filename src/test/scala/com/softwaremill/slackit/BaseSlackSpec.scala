package com.softwaremill.slackit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.softwaremill.slackit.testkit.SlackTestKit
import com.typesafe.config.ConfigFactory
import io.circe.Json
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterEach, Matchers}

import scala.concurrent.ExecutionContext

trait BaseSlackSpec extends AsyncFlatSpec with BeforeAndAfterEach with Matchers {
  implicit val system                = ActorSystem()
  implicit val materializer          = ActorMaterializer()
  implicit val ec: ExecutionContext  = ExecutionContext.Implicits.global
  implicit val exec: RequestExecutor = SlackTestKit.exec

  val config = new SlackConfig {
    override protected def rootConfig = ConfigFactory.load()
  }

  private def mergeOkField(source: Json, ok: Boolean): Json = {
    source.deepMerge(Json.obj(("ok", Json.fromBoolean(ok))))
  }

  def successfulResponse(source: Json): Json = {
    mergeOkField(source, ok = true)
  }

  def errorResponse(error: String): Json = {
    val errorField = Json.fromFields(Seq(("error", Json.fromString(error))))
    mergeOkField(errorField, ok = false)
  }

  override protected def afterEach() = {
    super.afterEach()
    SlackTestKit.clear()
  }
}
