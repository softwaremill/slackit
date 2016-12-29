package com.softwaremill.slackit.webapi

object Errors {
  abstract class SlackError(errorCode: String) extends Throwable(errorCode)
  case class InvalidClientId(code: String)     extends SlackError(code)
  case class BadClientSecret(code: String)     extends SlackError(code)

  def mapTo(error: String): SlackError = {
    error match {
      case "invalid_client_id" => InvalidClientId(error)
      case "bad_client_secret" => BadClientSecret(error)
      case _                   => throw new UnsupportedOperationException(s"Unknown slack error: $error")
    }
  }
}
