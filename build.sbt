import scala.scalanative.build.*

val scala3Version = "3.6.3"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "it.unibo.field4s"
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / homepage := Some(url("https://github.com/field4s/field4s"))
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
  "-Werror",
  "-Wunused:all",
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
  "-feature",
  "-language:strictEquality",
  "-language:implicitConversions",
)
ThisBuild / coverageEnabled := true
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

ThisBuild / libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats-core" % "2.13.0",
  "org.scalatest" %%% "scalatest" % "3.2.19" % Test,
)

lazy val commonTestSettings = Seq(
  Test / scalacOptions --= Seq(
    "-rewrite",
    "-Wunused:all",
    "-Wvalue-discard",
    "-Wnonunit-statement",
    "-Xcheck-macros",
  ),
)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .configs()
    .nativeSettings(
      nativeConfig ~= {
        _.withLTO(LTO.default)
          .withMode(Mode.releaseSize)
          .withGC(GC.immix)
      }
    )
    .jsSettings(
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= { _.withOptimizer(true) }
    )
  .settings(
    name := "core",
    sonatypeProfileName := "it.unibo.field4s",
    commonTestSettings,
  )

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .aggregate(core.jvm, core.js, core.native)
    .settings(
        name := "field4s",
        publish / skip := true,
        publishArtifact := false,
    )
