package it.unibo.scafi.integration

import java.nio.file.Path

import scala.util.Try

import it.unibo.scafi.integration.FileSystem
import it.unibo.scafi.integration.PlatformTest.{ Pattern, ProgramOutput, Substitution, SubstitutionBuilder }

import cats.syntax.traverse.toTraverseOps
import io.github.iltotore.iron.:|
import io.github.iltotore.iron.constraint.string.{ EndWith, StartWith }
import org.scalatest.matchers.should

trait PlatformTest extends should.Matchers with FileSystem:
  export PlatformTest.->
  export io.github.iltotore.iron.autoRefine

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
      programContent <- programUnderTest(testName).flatMap(read)
      templateContent <- read(templateFile)
      updatedContent = substitutions.foldLeft(programContent ++ templateContent):
        case (content, (pattern, substitution)) =>
          content.replace(pattern, substitution) match
            case newContent if newContent != content => newContent
            case _ => throw IllegalStateException(s"No pattern ${pattern} found to replace!")
      mainProgramPath = workingDir.resolve(templateFile.getFileName.toString.replace(".template", ""))
      _ <- write(mainProgramPath, updatedContent)
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
  def compile(workingDir: Path): Try[Unit]

  /** Run the program situated in the given [[workingDir]] and return its output. */
  def run(workingDir: Path): Try[ProgramOutput]

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
    private[PlatformTest] def add(key: Pattern, value: String): Unit = _substitutions += ((key, value))
    def substitutions: Set[Substitution] = _substitutions.view.toSet

  extension (pattern: Pattern)
    /** Substitutes the pattern with the given value in a template file. */
    infix inline def ->(value: String)(using builder: SubstitutionBuilder): Unit = builder.add(pattern, value)
