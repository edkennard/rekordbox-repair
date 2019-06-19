package com.vividlab.rekordbox

import java.io.{BufferedWriter, File, FileWriter}
import java.net.URI

import com.vividlab.rekordbox.analyse.AnalyserResult
import org.slf4j.LoggerFactory

object FileUtils {
  private val log = LoggerFactory.getLogger(getClass)

  /**
    * A location in rekordbox format ready to write out to XML with any special characters such as spaces encoded
    * to %20, and the rekordbox non-standard format for the host 'localhost':
    *
    * file://localhost/Users/User/Music/PioneerDJ/Demo%20Tracks/Demo%20Track%202.mp3
    * file://localhost/Users/User/Music/Name%20with%20#.mp3
    */
  def locationFromFile(file: File): String =
    file.toURI.toString
      .replace("file:", "file://localhost")
      .replace("%23", "#")

  /**
    * A File object converted from a rekordbox location, with special characters decoded.
    * Hashes need special treatment as they pass through the URI.
    *
    * file:///Users/User/Music/PioneerDJ/Demo Tracks/Demo Track 2.mp3
    * file:///Users/User/Music/Name with #.mp3
    *
    * @param location A location in rekordbox format extracted from XML
    */
  def fileFromLocation(location: String): File =
    new File(new URI(location
      .replace("file://localhost/", "file:///")
      .replace("#", "%23")
    ))

  /**
    * List all files in the given directory and all of its subdirectories.
    *
    * This could be refined to inspect the actual files for details like encoding method, bit depth and sampling frequency.
    * For more detail on rekordbox's supported formats, see pages 196 and 197 of https://rekordbox.com/_app/files/img/rekordbox5.5.0_manual_EN.pdf
    */
  def allSupportedFilesInDir(rootDir: File): Seq[File] = {
    val supportedTypes = Seq("mp3", "mp4", "m4a", "aif", "aiff", "fla", "flac", "wav")

    def supported(filename: String): Boolean = supportedTypes.exists { st => filename.toLowerCase.endsWith(s".$st") }

    def recursiveFilesInDir(dir: File): Array[File] = {
      val files = dir.listFiles
      files.filter(f => supported(f.getName)) ++ files.filter(_.isDirectory).flatMap(recursiveFilesInDir)
    }

    recursiveFilesInDir(rootDir).filterNot(_.isDirectory)
  }

  def writeTextFile(file: File, text: String): Unit = {
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(text)
    writer.close()
  }

  /**
    * From the given XML file, create a corresponding file for writing the detailed report.
    *
    * For example, if the output XML file is /rekordbox-fixed.xml, the report file will be /rekordbox-fixed.report.txt
    */
  def reportFileFromXmlFile(xmlFile: File): File = {
    val xmlFilePath = xmlFile.getAbsolutePath
    val filename = s"${xmlFilePath.substring(0, xmlFilePath.lastIndexOf("."))}.report.txt"
    new File(filename)
  }

  def writeReport(reportFile: File, result: AnalyserResult): Unit = {
    FileUtils.writeTextFile(reportFile, result.reportText())
    log.info(s"For a detailed report, see here: ${reportFile.getAbsolutePath}")
  }

}
