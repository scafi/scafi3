package it.unibo.scafi.integration

import java.nio.file.Path

import scala.io.Source
import scala.util.{ Try, Using }

import it.unibo.scafi
import it.unibo.scafi.integration.PlatformTest.MergeOrder.TemplateFirst

import cats.syntax.traverse.toTraverseOps
import io.github.iltotore.iron.:|
import io.github.iltotore.iron.constraint.string.{ EndWith, StartWith }
import org.scalatest.matchers.should

import scafi.integration.FileSystem
import scafi.integration.PlatformTest.{ MergeOrder, Pattern, ProgramOutput, Substitution, SubstitutionBuilder }

trait PlatformTest extends should.Matchers with OS with FileSystem:
  export PlatformTest.->
  export io.github.iltotore.iron.autoRefine

  /** The order in which the program under test and the template files are merged. */
  given mergeOrder: MergeOrder = compiletime.deferred

  /**
   * Runs a test for the specified program by:
   *   1. Creating a working directory from predefined templates.
   *   2. Applying all substitutions provided in the `addSubstitutions` block.
   *   3. Compiling and executing the program.
   * @param testName
   *   The name of the program being tested.
   * @param addSubstitutions
   *   A function block where template substitutions can be defined using the `->` operator.
   * @return
   *   The program's output, if the process completes successfully.
   */
  def testProgram(testName: String)(addSubstitutions: SubstitutionBuilder ?=> Unit): Try[ProgramOutput] =
    given builder: SubstitutionBuilder = SubstitutionBuilder()
    addSubstitutions
    for
      workingDir <- createTempDirectory(testName)
      _ <- resolveTemplates(testName, workingDir, builder.substitutions)
      _ <- compile(workingDir)
      out <- run(workingDir)
      _ = delete(workingDir)
    yield out.trim()

  private def resolveTemplates(testName: String, workingDir: Path, substitutions: Set[Substitution]): Try[Unit] =
    for
      templateFile <- copyTemplates(testName, workingDir)
      program <- programUnderTest(testName).flatMap(read)
      template <- read(templateFile)
      mergedContent = if mergeOrder == TemplateFirst then template ++ program else program ++ template
      resolvedContent = substitutions.foldLeft(mergedContent):
        case (content, (pattern, substitution)) =>
          content.replace(pattern, substitution) match
            case newContent if newContent != content => newContent
            case _ => throw IllegalStateException(s"No pattern $pattern found to replace!")
      mainProgramPath = workingDir.resolve(templateFile.getFileName.toString.replace(".template", ""))
      _ <- write(mainProgramPath, resolvedContent)
    yield ()

  private def copyTemplates(testName: String, workingDir: Path): Try[Path] =
    for
      files <- templates(testName)
      _ <- files.toList.traverse(p => copy(p, workingDir.resolve(p.getFileName)))
    yield files.find(_.getFileName.toString.contains("template")).getOrElse(throw IllegalStateException("No template!"))

  /** @return the path to the program under test, given its name. */
  def programUnderTest(testName: String): Try[Path]

  /** @return the set of template files from which bootstrapping the test working directory. */
  def templates(testName: String): Try[Set[Path]]

  /** Compile the program situated in the given [[workingDir]]. */
  def compile(workingDir: Path): Try[String]

  /** Run the program situated in the given [[workingDir]] and return its output. */
  def run(workingDir: Path): Try[String]

  /** Execute the given list of commands in the specified working directory. */
  def execute(workingDir: Path, commands: List[String]): Try[ProgramOutput] = Try:
    val process = new ProcessBuilder(commands*)
      .directory(workingDir.toFile)
      .start()
    process.waitFor() match
      case 0 => Using.resource(Source.fromInputStream(process.getInputStream))(_.mkString)
      case _ =>
        val err = Using.resource(Source.fromInputStream(process.getErrorStream))(_.mkString)
        throw new RuntimeException(s"Process failed (exit code ${process.exitValue()}): $err.")
end PlatformTest

object PlatformTest:

  /** The type of output produced by the program under test. */
  type ProgramOutput = String

  /** A pattern is a portion of text to be replaced in a template file in the form of `{{ var }}`. */
  type Pattern = String :| (StartWith["{{"] & EndWith["}}"])

  /** A substitution is a mapping from a pattern to a replacement string. */
  type Substitution = (Pattern, String)

  class SubstitutionBuilder:
    private var _substitutions = Set.empty[Substitution]
    private[PlatformTest] def add(key: Pattern, value: String): Unit = _substitutions += (key, value)
    def substitutions: Set[Substitution] = _substitutions.view.toSet

  extension (pattern: Pattern)
    /** Substitutes the pattern with the given value in a template file. */
    infix inline def ->(value: String)(using builder: SubstitutionBuilder): Unit = builder.add(pattern, value)

  /** The order in which the program under test and the template files are merged. */
  enum MergeOrder derives CanEqual:
    /** The program under test is appended after the templates. */
    case TemplateFirst

    /** The program under test is prepended before the templates. */
    case ProgramFirst
end PlatformTest
