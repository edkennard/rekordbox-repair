package com.vividlab.rekordbox

import java.io.File

import org.scalatest.{FreeSpec, Matchers}

class ConfigTest extends FreeSpec with Matchers with TestData {

  "Valid arguments" in {
    val args: Array[String] = Array(
      "-i", brokenLibraryXmlFile.getAbsolutePath,
      "-o", fixedLibraryXmlFile.getAbsolutePath,
      "-s", testLibraryDir.getAbsolutePath
    )
    val config = Config(args)
    config.inputXmlFile shouldBe brokenLibraryXmlFile
    config.outputXmlFile shouldBe fixedLibraryXmlFile
    config.searchDirectory shouldBe testLibraryDir
    config.outputRepairedTracksOnly shouldBe true
  }

  "Valid arguments with optional 'output relocated tracks only' argument disabled" in {
    val args: Array[String] = Array(
      "-i", brokenLibraryXmlFile.getAbsolutePath,
      "-o", fixedLibraryXmlFile.getAbsolutePath,
      "-s", testLibraryDir.getAbsolutePath,
      "-r", "false"
    )
    val config = Config(args)
    config.inputXmlFile shouldBe brokenLibraryXmlFile
    config.outputXmlFile shouldBe fixedLibraryXmlFile
    config.searchDirectory shouldBe testLibraryDir
    config.outputRepairedTracksOnly shouldBe false
  }

  "No arguments specified" in {
    val blankConfig = Config(None, None, None)

    assertThrows[IllegalStateException] {
      blankConfig.inputXmlFile
    }
    assertThrows[IllegalStateException] {
      blankConfig.outputXmlFile
    }
    assertThrows[IllegalStateException] {
      blankConfig.searchDirectory
    }
  }

  "Invalid arguments specified" in {
    assertThrows[IllegalStateException] {
      Config(Array[String]("blah", "blah"))
    }
  }

  "Invalid input XML file or search directory specified" in {
    val validXmlFile = brokenLibraryXmlFile
    val validDirectory = testLibraryDir
    val invalidXmlFile = new File("non-existent-file.xml")
    val invalidDirectory = new File("/NonExistentDir")

    assertThrows[IllegalArgumentException] {
      Config(Array(
        "-i", invalidXmlFile.getAbsolutePath,
        "-o", validXmlFile.getAbsolutePath,
        "-s", validDirectory.getAbsolutePath
      ))
    }

    assertThrows[IllegalArgumentException] {
      Config(Array(
        "-i", validXmlFile.getAbsolutePath,
        "-o", validXmlFile.getAbsolutePath,
        "-s", invalidDirectory.getAbsolutePath
      ))
    }
  }
}
