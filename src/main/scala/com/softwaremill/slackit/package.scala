package com.softwaremill

import akka.NotUsed
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import com.softwaremill.slackit.webapi.Errors
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.jawn._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
package object slackit {

  import de.heikoseeberger.akkahttpcirce.CirceSupport._

  type RequestExecutor = HttpRequest => Source[HttpResponse, NotUsed]

  type JSON = String

  private case class ErrorContainer(ok: Boolean, error: Option[String])

  object errorFlow extends StrictLogging {
    def apply()(implicit ec: ExecutionContext, mat: Materializer): Flow[HttpResponse, HttpResponse, NotUsed] =
      Flow[HttpResponse].mapAsync(1)(_.toStrict(10 seconds)).mapAsync(1) { response =>
        Unmarshal(response.entity).to[ErrorContainer].map {
          case container: ErrorContainer if container.ok => response
          case container: ErrorContainer if !container.ok =>
            logger.error(s"Error executing request: container: $container")
            throw container.error.map(Errors.mapTo).get
        }
      }
  }

  object asJsonFlow extends StrictLogging {
    def apply()(implicit ec: ExecutionContext, mat: Materializer): Flow[HttpResponse, JSON, NotUsed] = {
      Flow[HttpResponse].mapAsync(1)(_.entity.toStrict(10 seconds)).map(_.data.utf8String).log("json-flow")
    }
  }

  def circeDecoderFlow[C, T](extractor: C => T)(implicit decoder: Decoder[C]): Flow[JSON, T, NotUsed] = {
    Flow[JSON].map(decode[C](_) match {
      case Right(value) => extractor(value)
      case Left(e)      => throw e
    })
  }

  def defaultRequestExecutor(connection: Flow[HttpRequest, HttpResponse, _])(implicit ec: ExecutionContext,
                                                                             mat: Materializer) =
    (request: HttpRequest) => Source.single(request).via(connection).via(errorFlow())

}
