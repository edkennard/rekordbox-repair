package com.vividlab.rekordbox.analyse

import com.vividlab.rekordbox._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.slf4j.LoggerFactory

import scala.io.Source

class AnalyserTest extends AnyFreeSpec with Matchers with TestData with TestUtils {
  private val log = LoggerFactory.getLogger(getClass)

  "Analyse a broken rekordbox library" in {
    val config = Config(Array(
      "-i", brokenLibraryXmlFile.getAbsolutePath,
      "-o", fixedLibraryXmlFile.getAbsolutePath,
      "-s", testLibraryDir.getAbsolutePath,
      "-r", "false"
    ))

    Analyser.analyse(config) match {
      case Right(result) =>

        val lr = result.locateFileResults
        lr.ok.size shouldBe 2
        lr.relocated.size shouldBe 3
        lr.multipleLocations.size shouldBe 1
        lr.missing.size shouldBe 1

        if (OS.isMac) {
          result.filesNotInRekordBox.size shouldBe 2
          result.filesWithPathTooLong.size shouldBe 1
        } else {
          result.filesNotInRekordBox.size shouldBe 1
          result.filesWithPathTooLong.size shouldBe 0
        }

        // Output summary to log just for developer's benefit when inspecting test output. The composition of the summary
        // text sent to the log here is verified below when testing the full report
        log.info(result.logText())

        // Test the report's output
        FileUtils.writeReport(fixedLibraryReportFile, result)

        val expectedReportSource = Source.fromFile(expectedReportFile)
        val actualReportSource = Source.fromFile(fixedLibraryReportFile)

        val expectedReportText = expectedReportSource.getLines().mkString(OS.newLine)
        val actualReportText = actualReportSource.getLines().mkString(OS.newLine)

        expectedReportSource.close()
        actualReportSource.close()

        actualReportText shouldBe expectedReportText

      case Left(ex) =>
        fail(ex)
    }
  }

  "Reject invalid rekordbox XML" in {
    val config = Config(Array(
      "-i", missingAttributeLibraryXmlFile.getAbsolutePath,
      "-o", fixedLibraryXmlFile.getAbsolutePath,
      "-s", testLibraryDir.getAbsolutePath
    ))

    Analyser.analyse(config).isLeft shouldBe true
  }
}
