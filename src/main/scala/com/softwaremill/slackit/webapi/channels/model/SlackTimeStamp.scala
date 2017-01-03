package com.softwaremill.slackit.webapi.channels.model

import java.time.{Instant, ZoneId, ZonedDateTime}

case class SlackTimeStamp(dateTime: ZonedDateTime, messageId: String = "") {
  override def toString = {
    val dtString = dateTime.toEpochSecond.toString
    if (messageId.nonEmpty) s"$dtString.$messageId" else dtString
  }
}

object SlackTimeStamp {
  def apply(ts: String): SlackTimeStamp = {
    val parts: Array[String] = ts.split('.')
    val dateTime             = ZonedDateTime.ofInstant(Instant.ofEpochSecond(parts(0).toLong), ZoneId.systemDefault())
    if (ts.contains(".")) {
      new SlackTimeStamp(dateTime, parts(1))
    } else SlackTimeStamp(dateTime)
  }
}
