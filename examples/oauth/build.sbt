val slf4jVersion        = "1.7.21"
val logBackVersion      = "1.1.7"
val scalaLoggingVersion = "3.5.0"

val commonSettings = Seq(
  organization := "com.softwaremill",
  name := "slackit-oauth-example",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation")
)

val slf4jApi       = "org.slf4j" % "slf4j-api" % slf4jVersion
val logBackClassic = "ch.qos.logback" % "logback-classic" % logBackVersion
val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
val loggingStack   = Seq(slf4jApi, logBackClassic, scalaLogging)

val slackit = "com.softwaremill" %% "slackit" % "0.1.0-SNAPSHOT"

val dependencies = Seq(
  libraryDependencies ++= Seq(slackit) ++ loggingStack
)

val oauth = (project in file(".")).settings(commonSettings).settings(dependencies)
