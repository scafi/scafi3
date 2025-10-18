package it.unibo.scafi.integration

import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path, Paths, StandardOpenOption }

import scala.jdk.StreamConverters.StreamHasToScala
import scala.util.Try

trait FileSystem:

  /** Create a temporary directory with the given [[prefix]]. */
  def createTempDirectory(prefix: String): Try[Path] = Try(Files.createTempDirectory(prefix))

  /** Copy a file from the [[origin]] path to the [[destination]] path. */
  def copy(origin: Path, destination: Path): Try[Unit] = Try(Files.copy(origin, destination): Unit)

  /** @return the contents of the file at the given [[path]]. */
  def read(path: Path): Try[String] = Try(Files.readString(path, StandardCharsets.UTF_8))

  /** Write the given [[content]] to a file at the specified [[path]]. */
  def write(path: Path, content: String): Try[Unit] = Try:
    Files.writeString(path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE): Unit

  /** @return all files recursively found in the given [[path]] directory. */
  def findAll(path: Path): Try[Set[Path]] = Try(Files.walk(path).toScala(Seq).filter(_.toFile.isFile).toSet)

  /** Delete the file or directory at the given [[path]]. */
  def delete(path: Path): Try[Unit] = Try:
    if path.toFile.isDirectory
    then Files.walk(path).toScala(Seq).sortBy(_.toString)(using Ordering.String.reverse).foreach(Files.delete)
    else Files.delete(path)

  /** @return the path to a resource file named [[name]] situated in `src/test/resources`. */
  def resource(name: String): Try[Path] = Try(Paths.get(getClass.getClassLoader.getResource(name).toURI))

  /** @return the path to a file or directory situated in the project root, given its relative path [[entries]]. */
  def projectResource(entries: String*): Try[Path] = Try(Paths.get(System.getProperty("user.dir"), entries*))
end FileSystem
