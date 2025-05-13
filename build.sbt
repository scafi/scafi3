import scala.scalanative.build.*

val scala3Version = "3.7.0"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "it.unibo.scafi"
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / homepage := Some(url("https://github.com/scafi/scafi3"))
ThisBuild / licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / developers := List(
  Developer(
    "nicolasfara",
    "Nicolas Farabegoli",
    "nicolas.farabegoli@unibo.it",
    url("https://nicolasfarabegoli.it")
  ),
  Developer(
    "cric96",
    "Gianluca Aguzzi",
    "gianluca.aguzzi@unibo.it",
    url("https://github.com/cric96")
  ),
  Developer(
    "davidedomini",
    "Danide Domini",
    "davide.domini@unibo.it",
    url("https://github.com/davidedomini")
  )
)
ThisBuild / scalacOptions ++= Seq(
//  "-Werror",
//  "-Wunused:all",
  "-Wvalue-discard",
  "-Wnonunit-statement",
  "-Yexplicit-nulls",
  "-Wsafe-init",
  "-Ycheck-reentrant",
  "-Xcheck-macros",
  "-rewrite",
  "-indent",
  "-unchecked",
  "-explain",
  "-experimental",
  "-feature",
  "-language:strictEquality",
  "-language:implicitConversions",
  "-Wconf:msg=unused value of type org.scalatest.Assertion:s",
  "-Wconf:msg=unused value of type org.scalatest.compatible.Assertion:s",
  "-Wconf:msg=unused value of type org.specs2.specification.core.Fragment:s",
  "-Wconf:msg=unused value of type org.specs2.matcher.MatchResult:s",
  "-Wconf:msg=unused value of type org.scalamock:s",
)
ThisBuild / coverageEnabled := true
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

ThisBuild / libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats-core" % "2.13.0",
  "org.scalatest" %%% "scalatest" % "3.2.19" % Test,
  "org.scalamock" %%% "scalamock" % "7.1.0" % Test,
)

lazy val `scafi-core` = // crossProject(JSPlatform, JVMPlatform, NativePlatform)
//  .crossType(CrossType.Pure)
//  .in(file("core"))
//  .configs()
//    .nativeSettings(
//      nativeConfig ~= {
//        _.withLTO(LTO.default)
//          .withMode(Mode.releaseSize)
//          .withGC(GC.immix)
//      }
//    )
//    .jsSettings(
//      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
//      scalaJSUseMainModuleInitializer := true,
//      scalaJSLinkerConfig ~= { _.withOptimizer(true) }
//    )
  project
  .settings(
    name := "scafi-core",
    sonatypeProfileName := "it.unibo.scafi",
  )

val alchemistVersion = "42.1.0"
lazy val `alchemist-incarnation-scafi3` = project
  .settings(
    fork := true,
    name := "alchemist-incarnation-scafi3",
    libraryDependencies ++= Seq(
      "it.unibo.alchemist" % "alchemist" % alchemistVersion,
      "it.unibo.alchemist" % "alchemist-swingui" % alchemistVersion,
      "it.unibo.alchemist" % "alchemist-api" % alchemistVersion,
      "it.unibo.alchemist" % "alchemist-test" % alchemistVersion,
    ),
  )
//  .dependsOn(core.jvm)
  .dependsOn(`scafi-core`)


lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
//  .aggregate(core.jvm, core.js, core.native, `alchemist-incarnation`)
  .aggregate(`scafi-core` /*`alchemist-incarnation`*/)
    .settings(
        name := "scafi3",
        publish / skip := true,
        publishArtifact := false,
    )
