import sbt._
import Keys._

val commonSettings = Seq(
  organization := "com.softwaremill",
  name := "slackit",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation")
)

val akkaVersion         = "2.4.14"
val akkaHttpVersion     = "10.0.0"
val circeVersion        = "0.6.1"
val slf4jVersion        = "1.7.21"
val logBackVersion      = "1.1.7"
val scalaLoggingVersion = "3.5.0"

val circeCore    = "io.circe" %% "circe-core" % circeVersion
val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
val circeJawn    = "io.circe" %% "circe-jawn" % circeVersion
val circeDeps    = Seq(circeCore, circeGeneric, circeJawn)

val scalatest = "org.scalatest" %% "scalatest" % "3.0.1" % "it,test"
val testDeps  = Seq(scalatest)

val akkaHttpCore         = "com.typesafe.akka" %% "akka-http-core"  % akkaHttpVersion
val akkaHttpExperimental = "com.typesafe.akka" %% "akka-http"       % akkaHttpVersion
val akkaHttpCirceSupport = "de.heikoseeberger" %% "akka-http-circe" % "1.11.0"
val akkaActor            = "com.typesafe.akka" %% "akka-actor"      % akkaVersion
val akkaStream           = "com.typesafe.akka" %% "akka-stream"     % akkaVersion

val akkaDeps = Seq(akkaHttpCore, akkaHttpExperimental, akkaHttpCirceSupport, akkaActor, akkaStream)

val slf4jApi       = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingDeps   = Seq(slf4jApi, logBackClassic, scalaLogging)

val dependencies = Seq(
  libraryDependencies ++= akkaDeps ++ testDeps ++ circeDeps ++ loggingDeps
)

val slackit = (project in file(".")).settings(commonSettings).settings(dependencies).configs(IntegrationTest).settings( Defaults.itSettings : _*)
