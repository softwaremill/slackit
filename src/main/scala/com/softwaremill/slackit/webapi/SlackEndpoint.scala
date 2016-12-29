package com.softwaremill.slackit.webapi

import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.Uri.Query
import com.softwaremill.slackit.SlackConfig

trait SlackEndpoint {
  def config: SlackConfig

  def Get(endpoint: String, token: String, params: Map[String, String] = Map.empty) =
    RequestBuilding.Get(
      Uri(endpoint).withQuery(
        Query(Map("token" -> token) ++ params)
      ))
}
