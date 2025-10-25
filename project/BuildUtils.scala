object BuildUtils {

  sealed trait OS
  case object Windows extends OS
  case object MacOS extends OS
  case object Linux extends OS

  def os: OS = {
    val osName = System.getProperty("os.name").toLowerCase
    if (osName.contains("win")) Windows
    else if (osName.contains("mac")) MacOS
    else if (osName.contains("nux")) Linux
    else throw new Exception(s"Unsupported OS: $osName")
  }

  def nativeLibExtension: String = os match {
    case Windows => "dll"
    case MacOS => "dylib"
    case Linux => "so"
  }
}
