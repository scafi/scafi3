package it.unibo.scafi.integration

import java.nio.file.Path

import scala.util.Try

import it.unibo.scafi.integration.PlatformTest
import it.unibo.scafi.integration.PlatformTest.MergeOrder

import cats.syntax.all.catsSyntaxTuple5Semigroupal

trait CTests extends PlatformTest:

  override given mergeOrder: MergeOrder = MergeOrder.TemplateFirst

  override def programUnderTest(testName: String): Try[Path] = resource(s"c/$testName/program.c")

  override def templates(testName: String): Try[Set[Path]] = (
    resource(s"c/$testName").flatMap(findAll),
    projectResource("scafi3-polyglot-api", "native", "src", "main", "resources").flatMap(findAll),
    projectResource("scafi3-polyglot-api", "native", "target", "nativeLink").flatMap(findAll),
    resource("c/main.template.c"),
    resource("c/Makefile"),
  ).mapN(_ ++ _ ++ _ + _ + _)

  override def compile(workingDir: Path): Try[String] = execute(workingDir, List("make"))

  override def run(workingDir: Path): Try[String] =
    execute(workingDir, workingDir.resolve(if isWindows then ".\\main.exe" else "./main").toString :: Nil)
