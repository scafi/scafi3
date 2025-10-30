import org.scalajs.linker.interface.OutputPatterns
import scala.scalanative.build.{ BuildTarget, GC, LTO, Mode }
import sbtcrossproject.CrossProject
import BuildUtils.{ MacOS, Windows, nativeLibExtension, os }
import bindgen.interface.Binding

val scala3Version = "3.7.3"

ThisBuild / scalaVersion := scala3Version
ThisBuild / name := "scafi3"
ThisBuild / organization := "it.unibo.scafi"
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
    "tassiluca",
    "Luca Tassinari",
    "luca.tassinari.2000@gmail.com",
    url("https://github.com/tassiluca")
  ),
)
val commonScalacOptions = Seq(
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
  "-encoding", "UTF-8",
  "-feature",
  "-preview",
  "-deprecation",
  "-language:strictEquality",
  "-language:implicitConversions",
  "-language:experimental.saferExceptions",
  "-language:experimental.modularity",
  "-Wconf:msg=unused value of type org.scalatest.Assertion:s",
  "-Wconf:msg=unused value of type org.scalatest.compatible.Assertion:s",
  "-Wconf:msg=unused value of type org.specs2.specification.core.Fragment:s",
  "-Wconf:msg=unused value of type org.specs2.matcher.MatchResult:s",
  "-Wconf:msg=unused value of type org.scalamock:s",
)

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

val ExclusiveTestTag = Tags.Tag("exclusive-test")
Global / concurrentRestrictions += Tags.exclusive(ExclusiveTestTag)

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.13.0",
    "org.scalactic" %%% "scalactic" % "3.2.19",
    "io.github.iltotore" %%% "iron" % "3.2.0",
    "com.outr" %%% "scribe" % "3.17.0",
    "org.scalatest" %%% "scalatest" % "3.2.19" % Test,
    "org.scalatestplus" %%% "scalacheck-1-18" % "3.2.19.0" % Test,
  ),
  scalacOptions ++= commonScalacOptions,
)

lazy val commonNativeSettings = Seq(
  nativeConfig := {
    // macOS requires an additional linking option to correctly set the runtime path of the dynamic
    // library using the `-rpath` option. For more information, see https://stackoverflow.com/a/66284977.
    val additionalLinkingOptions = if (os == MacOS)
      Seq(s"-Wl,-install_name,'@rpath/lib${(ThisBuild / name).value}.dylib'")
    else Nil
    nativeConfig.value
      .withLTO(LTO.full)
      .withMode(Mode.releaseSize)
      .withGC(GC.immix)
      .withBuildTarget(BuildTarget.libraryDynamic)
      .withBaseName((ThisBuild / name).value)
      .withLinkingOptions(nativeConfig.value.linkingOptions ++ additionalLinkingOptions)
      .withCheck(true)
      .withCheckFeatures(true)
      .withCheckFatalWarnings(true)
  },
  Compile / nativeLink := { // TODO: extract in a nicer dsl into project with additional linking options for macos
    val out = (Compile / nativeLink).value
    val linkerOutputDir = target.value / "nativeLink"
    IO.createDirectory(linkerOutputDir)
    val libName = (ThisBuild / name).value
    val prefix = if (os == Windows) "" else "lib"
    val targetFile = linkerOutputDir / s"$prefix$libName.$nativeLibExtension"
    IO.move(out, targetFile)
    if (os == Windows) {
      val libFile = out.getParentFile / s"$libName.lib"
      IO.move(libFile, linkerOutputDir / s"$libName.lib")
    }
    targetFile
  },
  scalacOptions ++= Seq("-Wconf:msg=unused import&src=.*[\\\\/]src_managed[\\\\/].*:silent"),
  coverageEnabled := false,
)

lazy val commonJsSettings = Seq(
  scalaJSLinkerConfig ~= {
    _.withModuleKind(ModuleKind.ESModule)
      .withOutputPatterns(OutputPatterns.fromJSFile("%s.mjs"))
      .withOptimizer(true)
      .withMinify(true)
      .withCheckIR(true)
  },
  Compile / fastLinkJS / scalaJSLinkerOutputDirectory := target.value / "fastLinkJS",
  Compile / fullLinkJS / scalaJSLinkerOutputDirectory := target.value / "fullLinkJS",
  coverageEnabled := false,
)

lazy val `scafi3-core` = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("scafi3-core"))
  .configs()
  .nativeSettings(commonNativeSettings)
  .jsSettings(commonJsSettings)
  .settings(commonSettings)
  .settings(
    name := "scafi3-core",
  )

lazy val `scafi3-distributed` = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("scafi3-distributed"))
  .dependsOn(`scafi3-core` % "compile->compile;test->test")
  .nativeSettings(commonNativeSettings)
  .jsSettings(commonJsSettings)
  .settings(commonSettings)
  .settings(
    name := "scafi3-distributed",
    libraryDependencies ++= Seq(
      "io.bullet" %%% "borer-core" % "1.16.1",
      "io.bullet" %%% "borer-derivation" % "1.16.1",
    ),
  )

lazy val `scafi3-mp-api` = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("scafi3-mp-api"))
  .dependsOn(`scafi3-core` % "compile->compile;test->test", `scafi3-distributed`)
  .enablePlugins(BindgenPlugin)
  .nativeSettings(
    commonNativeSettings,
    bindgenBindings += Binding(
      header = (Compile / resourceDirectory).value / "include" / "scafi3.h",
      packageName = "it.unibo.scafi.nativebindings"
    )
  )
  .jsSettings(commonJsSettings)
  .settings(
    commonSettings,
    name := "scafi3-mp-api",
    libraryDependencies ++= Seq(
      "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
    ),
  )

lazy val `scafi3-integration` = project
  .in(file("scafi3-integration"))
  .dependsOn(`scafi3-distributed`.jvm % "compile->compile;test->test")
  .settings(
    commonSettings,
    publish / skip := true,
    Test / test := (Test / test)
      .dependsOn(`scafi3-mp-api`.js / Compile / fullLinkJS, `scafi3-mp-api`.native / Compile / nativeLink)
      .tag(ExclusiveTestTag)
      .value,
  )

val alchemistVersion = "42.3.15"
lazy val `alchemist-incarnation-scafi3` = project
  .settings(commonSettings)
  .settings(
    fork := true,
    name := "alchemist-incarnation-scafi3",
    libraryDependencies ++= Seq(
      "it.unibo.alchemist" % "alchemist" % alchemistVersion,
      "it.unibo.alchemist" % "alchemist-api" % alchemistVersion,
      "it.unibo.alchemist" % "alchemist-euclidean-geometry" % alchemistVersion,
      "org.scala-lang" %% "scala3-compiler" % scala3Version,
      "org.scalatest" %%% "scalatest" % "3.2.19" % Test,
    ),
  )
  .dependsOn(`scafi3-core`.jvm)

lazy val example = project
  .settings(
    name := "scafi3-example",
    publish / skip := true,
    publishArtifact := false,
    libraryDependencies ++= Seq(
      "it.unibo.alchemist" % "alchemist-swingui" % alchemistVersion,
    ),
    scalacOptions ++= Seq(
      "-language:experimental.saferExceptions"
    ),
  )
  .dependsOn(`scafi3-core`.jvm, `alchemist-incarnation-scafi3`)

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaUnidocPlugin)
  .aggregate(`alchemist-incarnation-scafi3`, `scafi3-integration`)
  .aggregate(crossProjects(`scafi3-core`, `scafi3-distributed`, `scafi3-mp-api`).map(_.project)*)
  .settings(
    name := (ThisBuild / name).value,
    publish / skip := true,
    publishArtifact := false,
  )

def crossProjects(crossProjects: CrossProject*) = crossProjects.flatMap(cp => Seq(cp.js, cp.jvm, cp.native))
