import bindgen.interface.Binding
import bindgen.plugin.BindgenPlugin
import sbt.*
import sbt.Keys.*

import scala.sys.process.Process
import scala.util.Try

/**
 * SBT plugin providing a `generateBindings` task to produce native bindings using the Scala Native Bindgen plugin.
 *
 * The task runs inside a Docker container to ensure consistent environment setup and avoid local dependencies,
 * such as specific (old) Clang versions.
 */
object NativeBindingsUtils extends AutoPlugin {

  private val dockerImageName = "sn-bindgen-builder"
  private val envVarName = "IN_BINDGEN_DOCKER"

  object autoImport {
    val generateBindings = taskKey[Seq[File]]("Generate native bindings using Docker")
    val bindgenDockerfile = settingKey[File]("Path to the Dockerfile for bindgen")
    val nativeBindings = settingKey[Seq[Binding]]("Native bindings configuration")
  }

  import autoImport.*

  private case class Config(
    bindings: Seq[Binding],
    buildDir: File,
    cacheDir: File,
    dockerfile: File,
    projectId: String,
    rootDir: File,
  )

  override def trigger = noTrigger

  override def requires: Plugins = BindgenPlugin

  override lazy val projectSettings = Seq(
    bindgenDockerfile := (Compile / resourceDirectory).value / "Dockerfile.bindgen",
    nativeBindings := Seq.empty,
    // Inside Docker use nativeBindings, on host use empty to prevent bindgen from running
    BindgenPlugin.autoImport.bindgenBindings := (if (inDocker) nativeBindings.value else Seq.empty),
    generateBindings := generateBindingsTask(
      Config(
        bindings = nativeBindings.value,
        buildDir = (Compile / sourceManaged).value,
        cacheDir = streams.value.cacheDirectory,
        dockerfile = bindgenDockerfile.value,
        projectId = thisProjectRef.value.project,
        rootDir = (ThisBuild / baseDirectory).value
      )
    )(streams.value.log),
    // Inside Docker keep bindgen's generators, on host use only Docker-based generator
    Compile / sourceGenerators := (if (inDocker) (Compile / sourceGenerators).value else Seq(generateBindings.taskValue))
  )

  private def inDocker: Boolean = sys.env.contains(envVarName)

  private def generateBindingsTask(ctx: Config)(implicit log: Logger): Seq[File] = {
    val existingBindings = generatedFiles(ctx.buildDir)
    val missingHeaders = ctx.bindings.map(_.headerFile).filterNot(_.exists())
    if (isCI && existingBindings.nonEmpty) { // Reuse existing bindings in CI if available
      log.info(s"CI environment: Found ${existingBindings.size} existing binding files.")
      existingBindings
    } else if (missingHeaders.nonEmpty) {
      log.warn("Header files not found.")
      Seq.empty
    } else {
      generateWithDocker(ctx)
    }
  }

  private def isCI: Boolean = sys.env.get("CI").contains("true")

  private def generateWithDocker(config: Config)(implicit log: Logger): Seq[File] = {
    val headers = config.bindings.map(_.headerFile).toSet
    FileFunction.cached(cacheBaseDirectory = config.cacheDir / "bindgen") { _ =>
      if (!isDockerAvailable) {
        sys.error("Docker is needed to generate native bindings but is not available")
      }
      if (!imageExists(dockerImageName)) {
        buildImage(dockerImageName, config.dockerfile)
      }
      val tempHome = config.rootDir / "target" / "docker-home"
      IO.createDirectory(tempHome)
      runContainer(
        imageName = dockerImageName,
        command = s"sbt ${config.projectId}/bindgenGenerateScalaSources",
        workDir = config.rootDir,
        volumes = Map(config.rootDir -> "/project", tempHome -> "/home/sbtuser"),
        envVars = Map("HOME" -> "/home/sbtuser", envVarName -> "true"),
      )
      generatedFiles(config.buildDir).toSet
    }(headers).toSeq
  }

  private def isDockerAvailable: Boolean = Try(Process("docker --version").! == 0).getOrElse(false)

  private def generatedFiles(managedDir: File): Seq[File] = (managedDir ** "*.scala").get

  private def imageExists(imageName: String): Boolean = Process(s"docker image inspect $imageName").! == 0

  private def buildImage(imageName: String, dockerfile: File)(implicit log: Logger): Unit = {
    log.info(s"Building Docker image '$imageName'. This may take a while but will be done only once...")
    val command = s"docker build -f ${dockerfile.absolutePath} -t $imageName ${dockerfile.getParentFile.absolutePath}"
    val exitCode = Process(command).!
    if (exitCode != 0) sys.error("Docker image build failed")
  }

  private def runContainer(
    imageName: String,
    command: String,
    workDir: File,
    volumes: Map[File, String] = Map.empty,
    envVars: Map[String, String] = Map.empty,
  )(implicit log: Logger): Unit = {
    val userId = sys.env.get("UID").orElse(Try(Process("id -u").!!.trim).toOption).getOrElse("1000")
    val groupId = sys.env.get("GID").orElse(Try(Process("id -g").!!.trim).toOption).getOrElse("1000")
    val mounts = volumes.map { case (hostPath, destPath) => s"-v ${hostPath.absolutePath}:$destPath" }.mkString(" ")
    val envVarArgs = envVars.map { case (key, value) => s"-e $key=$value" }.mkString(" ")
    val dockerCmd = s"docker run --rm --user $userId:$groupId $mounts $envVarArgs $imageName $command"
    val exitCode = Process(Seq("sh", "-c", dockerCmd), cwd = workDir).!
    if (exitCode != 0) sys.error(s"Docker container $imageName execution failed")
  }

}
