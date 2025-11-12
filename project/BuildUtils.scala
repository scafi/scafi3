import sbt.*

object BuildUtils {

  sealed trait OS
  case object Windows extends OS
  case object MacOS extends OS
  case object Linux extends OS

  /** @return the current operating system. */
  def os: OS = {
    val osName = System.getProperty("os.name").toLowerCase
    if (osName.contains("win")) Windows
    else if (osName.contains("mac")) MacOS
    else if (osName.contains("nux")) Linux
    else throw new Exception(s"Unsupported OS: $osName")
  }

  /** @return the native library file extension for the current OS. */
  def nativeLibExtension: String = os match {
    case Windows => "dll"
    case MacOS => "dylib"
    case Linux => "so"
  }

  /**
   * Additional linking options required for macOS to correctly set the runtime path of the dynamic library.
   * @param libName the base name of the library
   * @see https://stackoverflow.com/a/66284977
   */
  def macosLinkingOptions(libName: String): Seq[String] = os match {
    case MacOS => Seq(s"-Wl,-install_name,'@rpath/lib$libName.dylib'")
    case _ => Seq.empty
  }

  /**
   * Moves native library files to a version-agnostic directory.
   * @param libraryFile the linked native library file
   * @param targetDir the target directory where to move the library
   * @param libraryName the base name of the library
   */
  def moveNativeLibrary(libraryFile: File, targetDir: File, libraryName: String): File = {
    val outputDir = targetDir / "nativeLink"
    val prefix = if (os == Windows) "" else "lib"
    val mainLib = moveToDir(libraryFile, outputDir, s"$prefix$libraryName.$nativeLibExtension")
    if (os == Windows) {
      val importLib = libraryFile.getParentFile / s"$libraryName.lib"
      moveToDir(importLib, outputDir, s"$libraryName.lib")
    }
    mainLib
  }

  /** Moves a file to a target directory, creating it if necessary. */
  def moveToDir(sourceFile: File, targetDir: File, targetName: String): File = {
    IO.createDirectory(targetDir)
    val targetFile = targetDir / targetName
    IO.move(sourceFile, targetFile)
    targetFile
  }
}
