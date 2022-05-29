package com.vividlab.rekordbox.repair

import com.vividlab.rekordbox.analyse.Analyser
import com.vividlab.rekordbox.{Config, TestData, TestUtils}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.xml.XML

class RepairerTest extends AnyFreeSpec with Matchers with TestData with TestUtils {

  "Rewrite entire rekordbox XML file with repaired tracks" in {
    val config = Config(Array(
      "-i", brokenLibraryXmlFile.getAbsolutePath,
      "-o", fixedLibraryXmlFile.getAbsolutePath,
      "-s", testLibraryDir.getAbsolutePath,
      "-r", "false"
    ))

    Analyser.analyse(config) match {
      case Right(result) =>
        Repairer.repair(config, result)
        XML.loadFile(fixedLibraryXmlFile) shouldBe XML.loadFile(expectedFixedLibraryXmlFile)
      case Left(ex) =>
        fail(ex)
    }
  }

  "Write only repaired tracks and their related playlists to rekordbox XML file" in {
    val config = Config(Array(
      "-i", brokenLibraryXmlFile.getAbsolutePath,
      "-o", fixedLibraryXmlFile.getAbsolutePath,
      "-s", testLibraryDir.getAbsolutePath,
      "-r", "true"
    ))

    Analyser.analyse(config) match {
      case Right(result) =>
        Repairer.repair(config, result)

        // This helper function is needed specifically for this test because comparing directly using XML.loadFile per
        // test "End-to-end" fails the test, even if the XML is good. It might be due to a lot of white space in the
        // transformed XML
        xmlFilesMatch(fixedLibraryXmlFile, expectedFixedLibraryRepairedOnlyXmlFile) shouldBe true

      case Left(ex) =>
        fail(ex)
    }
  }
}
