package com.softwaremill.slackit.testkit

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

case class SlackResponder(path: String, statusCode: StatusCode = StatusCodes.OK, payloadJson: String = "") {
  def withStatusCode(sc: StatusCode) = this.copy(statusCode = sc)
  def withPayload(pl: String)        = this.copy(payloadJson = pl)
  def commit()                       = SlackTestKit.commit(this)
}
