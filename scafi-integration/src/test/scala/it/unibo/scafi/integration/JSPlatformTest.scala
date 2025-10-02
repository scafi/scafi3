package it.unibo.scafi.integration

import java.nio.file.{ Path, Paths }

import scala.io.Source
import scala.util.{ Success, Try, Using }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.integration.PlatformTest

import cats.syntax.all.catsSyntaxTuple4Semigroupal

trait JSPlatformTest extends PlatformTest:

  override def programUnderTest(testName: String): Try[Path] = resource(s"js/$testName/program.mjs")

  override def templates(testName: String): Try[Set[Path]] = (
    resource(s"js/$testName").flatMap(findAll),
    resource("js/main.template.mjs"),
    resource("js/package.json"),
    resource("js/build-protos.js"),
  ).mapN(_ + _ + _ + _)

  override def compile(workingDir: Path): Try[Unit] = Success(())

  override def run(workingDir: Path): Try[String] = Try:
    val npmCommand = if System.getProperty("os.name").toLowerCase.contains("windows") then "npm.cmd" else "npm"
    val process = new ProcessBuilder(npmCommand, "start", "--silent")
      .directory(workingDir.toFile)
      .tap(_.environment.put("SCAFI3", scafiJsBundlePath))
      .start()
    process.waitFor() match
      case 0 => Using.resource(Source.fromInputStream(process.getInputStream))(_.mkString)
      case _ =>
        val err = Using.resource(Source.fromInputStream(process.getErrorStream))(_.mkString)
        throw new RuntimeException(s"Node process failed: $err")

  private def scafiJsBundlePath = Paths
    .get(System.getProperty("user.dir"), "scafi-mp-api", "js", "target", "fullLinkJS", "main.mjs")
    .toAbsolutePath
    .toUri
    .toString

end JSPlatformTest
