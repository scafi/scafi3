package it.unibo.scafi.integration

import java.nio.file.Path

import scala.util.{ Success, Try }

import it.unibo.scafi.integration.PlatformTest
import it.unibo.scafi.integration.PlatformTest.MergeOrder

import cats.syntax.all.catsSyntaxTuple5Semigroupal

trait JSTests extends PlatformTest:

  override given mergeOrder: MergeOrder = MergeOrder.ProgramFirst

  override def programUnderTest(testName: String): Try[Path] = resource(s"js/$testName/program.mjs")

  override def templates(testName: String): Try[Set[Path]] = (
    resource(s"js/$testName").flatMap(findAll),
    resource("js/app.template.mjs"),
    resource("js/package.json"),
    resource("js/build-protos.js"),
    projectResource("scafi3-polyglot-api", "js", "target", "fullLinkJS", "main.mjs"),
  ).mapN(_ + _ + _ + _ + _)

  override def compile(workingDir: Path): Try[String] = Success("JS does not require compilation")

  override def run(workingDir: Path): Try[String] =
    execute(workingDir, (if isWindows then "npm.cmd" else "npm") :: "start" :: "--silent" :: Nil)
