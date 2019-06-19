package com.vividlab.rekordbox

import java.io.File
import java.nio.file.Files

import scala.io.Source
import scala.xml.XML

trait TestData {

  private val tempDir = Files.createTempDirectory("rekordbox-repair")
  tempDir.toFile.deleteOnExit()

  private def tempFile(filename: String) = {
    val file = new File(s"$tempDir/$filename")
    file.deleteOnExit()
    file
  }

  // Library containing some sample files, some of which are based on the factory set of tracks loaded into
  // rekordbox after first installing it
  private val testLibraryDirPath: String = getClass.getResource("/test-library").getFile
  val testLibraryDir: File = new File(testLibraryDirPath)

  // Test XML files
  val brokenLibraryXmlFile: File = xmlFromTemplate("broken-library.xml")
  val expectedFixedLibraryXmlFile: File = xmlFromTemplate("expected-fixed-library.xml")
  val expectedFixedLibraryRepairedOnlyXmlFile: File = xmlFromTemplate("expected-fixed-library-repaired-only.xml")
  val missingAttributeLibraryXmlFile: File = xmlFromTemplate("missing-attribute-library.xml")
  val fixedLibraryXmlFile: File = tempFile("fixed-library.xml")

  // Test report files
  val fixedLibraryReportFile: File = FileUtils.reportFileFromXmlFile(fixedLibraryXmlFile)
  fixedLibraryReportFile.deleteOnExit()
  val expectedReportFile: File = reportFromTemplate(if (OS.isWindows) "expected-report-windows.txt" else "expected-report-macos.txt")

  // Create file with a long name only for testing in MacOS. If we have a file like this permanently in test-resources,
  // Windows will fail to download it when pulling the repo from Git. Windows has a limit for the full path of a file of
  // around 260 characters, which is probably why rekordbox limit it as well, to ensure collections are portable between
  // MacOS and Windows
  if (OS.isMac) {
    val file = new File(s"$testLibraryDir/File with a really really really really really really really really really really really really really really really really really really really really really really really long name.mp3")
    file.createNewFile()
  }

  /**
    * Generates a test XML file from the given template, with all track file locations updated to point to
    * the 'test-classes/test-library' resource dir where the sample music files have been copied, which will have a
    * different path depending on each machine the tests are run on.
    *
    * @param filename Name of the template XML file to open and transform, as well as the generated XML file to write
    * @return A reference to the generated temporary XML file
    */
  private def xmlFromTemplate(filename: String): File = {
    val xmlLines = Source.fromResource(s"xml-templates/$filename").getLines()
    val xmlLinesCorrectPaths = xmlLines.map(_.replace("{testLibraryDirectory}", testLibraryDirPath))
    val xml = XML.loadString(xmlLinesCorrectPaths.mkString(OS.newLine))

    val xmlFile = tempFile(filename)
    XML.save(xmlFile.getAbsolutePath, xml.head, "UTF-8", xmlDecl = true)
    xmlFile
  }

  private def reportFromTemplate(filename: String): File = {
    val reportLines = Source.fromResource(s"report-templates/$filename").getLines()
    val reportLinesCorrectPaths = reportLines.map(_.replace("{testLibraryDirectory}", testLibraryDir.getCanonicalPath))
    val report = reportLinesCorrectPaths.mkString(OS.newLine)

    val reportFile = tempFile(filename)
    FileUtils.writeTextFile(reportFile, report)
    reportFile
  }
}
