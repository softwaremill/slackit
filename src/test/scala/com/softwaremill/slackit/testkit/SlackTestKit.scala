package com.softwaremill.slackit.testkit

import akka.NotUsed
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.softwaremill.slackit

import scala.concurrent.ExecutionContext

object SlackTestKit {

  var responders = List.empty[SlackResponder]

  private[testkit] def commit(responder: SlackResponder): Unit = responders = responders :+ responder

  def exec(request: HttpRequest)(implicit materializer: Materializer,
                                 ec: ExecutionContext): Source[HttpResponse, NotUsed] = {
    val responder = responders.find(responder => request.uri.path.startsWith(Uri.Path(responder.path)))
    val resp      = responder.getOrElse(throw new IllegalStateException(s"No responder configured for ${request.uri.path}"))
    val response =
      HttpResponse(status = resp.statusCode, entity = HttpEntity(ContentTypes.`application/json`, resp.payloadJson))
    responders = responders diff List(resp)
    Source.single(response).via(slackit.errorFlow())
  }

  def clear(): Unit = {
    responders = List.empty
  }
}
