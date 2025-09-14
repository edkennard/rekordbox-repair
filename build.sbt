import NativePackagerHelper.*
import sbt.io.IO.unzipURL
import java.net.URL

enablePlugins(SbtLicenseReport, JavaAppPackaging, WindowsPlugin)

name := "rekordbox-repair"
version := "0.6"
scalaVersion := "2.13.16"
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
)

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.13"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test

// Packaging config and mappings
//
// On MacOS run "sbt clean test dumpLicenseReport universal:packageZipTarball", which will keep executable permissions intact where packageBin doesn't
// On Windows run "sbt clean test dumpLicenseReport windows:packageBin" to generate a WIX-based setup wizard

maintainer := "Ed Kennard <ed_kennard@yahoo.com>"
packageSummary := "rekordbox Repair Tool"
packageDescription := """Command line tool to help users of Pioneer's rekordbox DJ software clean up their collections then keep them that way"""

val osName = System.getProperty("os.name").toLowerCase
val osArch = System.getProperty("os.arch").toLowerCase
val isMac = osName.startsWith("mac")
val isWindows = osName.startsWith("win")

Universal / mappings ++= {
  val log = streams.value.log

  if (!isMac && !isWindows)
    throw new IllegalArgumentException("Packaging is only configured for macOS and Windows, since rekordbox only exists on those platforms")

  val jreBase = "https://cdn.azul.com/zulu/bin"
  val jreVersion = "zulu17.60.17-ca-jre17.0.16"
  val jreOs = if (isMac) "macosx" else "win"
  val jreArch = if (osArch == "amd64") "x64" else osArch
  val jre = s"$jreVersion-${jreOs}_$jreArch"
  val jreUrl = s"$jreBase/$jre.zip"

  log.info(s"Downloading and unzipping JRE from $jreUrl...")
  unzipURL(new URL(jreUrl), Path("target/jre").asFile)

  val jreDir = if (isMac)
    Path(s"target/jre/$jre/zulu-17.jre/Contents/Home").asFile
  else
    Path(s"target/jre/$jre").asFile

  if (!jreDir.exists)
    throw new IllegalArgumentException(s"The packaged JRE is not in the expected structure, wasn't found in ${jreDir.getPath}")

  log.info(s"Adding JRE to package from $jreDir...")
  contentOf(jreDir).map { case (src, destination) => src -> s"jre/$destination" }
}

Universal / mappings ++= {
  val licenseReportsDir = target.value / "license-reports"
  streams.value.log.info(s"Adding license reports to package from $licenseReportsDir...")
  directory(licenseReportsDir)
}


// For non-Windows packages set JAVA_HOME to use our packaged JRE in /jre
val nonWindowsJavaHome = if (!isWindows)
  Seq("-java-home ${app_home}/../jre")
else
  Seq()

Universal / javaOptions ++= nonWindowsJavaHome


// Windows packaging using WIX toolset v3 installed from the archive at https://github.com/wixtoolset/wix3/releases
Windows / name := s"rekordbox-repair-${version.value}" // Name of generated MSI file
wixProductLicense := Some(new sbt.File("LICENSE.rtf"))

makeBatScripts := {
  // Custom .bat script generation to set JAVA_HOME on Windows package to use our packaged JRE in /jre
  // Thanks to solution found here: https://github.com/sbt/sbt-native-packager/issues/1070
  val batScripts = makeBatScripts.value
  val log = streams.value.log

  batScripts.map(_._1).foreach{batScript =>
    log.info(s"Updating $batScript to use our packaged JRE...")

    val newLines = IO.readLines(batScript).flatMap {
      case s @ "set \"APP_LIB_DIR=%APP_HOME%\\lib\\\"" =>
        Seq(s, "set \"JAVA_HOME=%APP_HOME%\\jre\"")
      case s => Seq(s)
    }

    IO.writeLines(batScript, newLines)
    log.info(s"Successfully updated $batScript.")
  }

  batScripts
}
