import NativePackagerHelper._

enablePlugins(SbtLicenseReport, JavaAppPackaging, WindowsPlugin)

name := "rekordbox-repair"
version := "0.9"
scalaVersion := "2.13.8"
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
)

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0"
libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.11"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test

// Packaging config and mappings
//
// On MacOS run "sbt clean test dumpLicenseReport universal:packageZipTarball", which will keep executable permissions intact where packageBin doesn't
// On Windows run "sbt clean test dumpLicenseReport windows:packageBin" to generate a WIX-based setup wizard

maintainer := "Ed Kennard <ed_kennard@yahoo.com>"
packageSummary := "rekordbox Repair Tool"
packageDescription := """Command line tool to help users of Pioneer's rekordbox DJ software clean up their collections then keep them that way"""

val osName = System.getProperty("os.name").toLowerCase
val isMac = osName.startsWith("mac")
val isWindows = osName.startsWith("win")

Universal / mappings ++= {
  val jreDir = if (isMac)
    Path("/Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home/jre").asFile
  else if (isWindows)
    Path("C:\\Program Files\\Java\\jdk1.8.0_211\\jre").asFile
  else
    throw new IllegalArgumentException("Packaging is only configured for MacOS and Windows, since rekordbox only exists on those platforms")

  if (!jreDir.exists)
    throw new IllegalArgumentException("The required JDK is not present on this system - please install JDK 1.8.0_211")

  streams.value.log.info(s"Adding JRE to package from $jreDir...")

  directory(jreDir)
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


// Windows packaging using WIX toolset
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
