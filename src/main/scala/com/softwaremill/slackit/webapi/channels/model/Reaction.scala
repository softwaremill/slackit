package com.softwaremill.slackit.webapi.channels.model

case class Reaction(name: String, users: List[String], count: Int)
