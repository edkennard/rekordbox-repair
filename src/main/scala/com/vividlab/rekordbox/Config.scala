package com.vividlab.rekordbox

import java.io.File

case class Config(
  inputXmlFile_ : Option[File] = None,
  outputXmlFile_ : Option[File] = None,
  searchDirectory_ : Option[File] = None,
  outputRepairedTracksOnly: Boolean = true
) {
  def inputXmlFile: File = inputXmlFile_.getOrElse {
    throw new IllegalStateException("Input rekordbox XML file not provided")
  }
  def outputXmlFile: File = outputXmlFile_.getOrElse {
    throw new IllegalStateException("Output rekordbox XML file not provided")
  }
  def searchDirectory: File = searchDirectory_.getOrElse {
    throw new IllegalStateException("Search directory not provided")
  }
}

case object Config {

  private val optionsParser = new scopt.OptionParser[Config]("rekordbox-repair") {
    opt[File]('i', "input-xml-file") valueName "<file>" action { (x, c) =>
      c.copy(inputXmlFile_ = Some(x))
    } text "Input rekordbox XML file to analyse, e.g. '-i /Users/You/Documents/rekordbox/library.xml'"

    opt[File]('o', "output-xml-file") valueName "<file>" action { (x, c) =>
      c.copy(outputXmlFile_ = Some(x))
    } text "Output rekordbox XML file to write fixed version to, e.g. '-o /Users/You/Documents/rekordbox/library-fixed.xml'"

    opt[File]('s', "search-directory") valueName "<file>" action { (x, c) =>
      c.copy(searchDirectory_ = Some(x))
    } text "Directory to search for missing files to relocate, e.g. '-s /Users/You/Music/iTunes'"

    opt[Boolean]('r', "output-repaired-tracks-only") valueName "<boolean>" action { (x, c) =>
      c.copy(outputRepairedTracksOnly = x)
    } text "Only write repaired tracks to the output rekordbox XML file, rather than the entire library. By default set to true, set to false if you want to write everything in order to rebuild your entire library"

    help("help") text "Prints this usage text"
  }

  def apply(args: Array[String]): Config = optionsParser.parse(args, Config()) match {
    case Some(config) =>
      if (!config.inputXmlFile.exists)
        throw new IllegalArgumentException(s"Input rekordbox XML file doesn't exist: ${config.inputXmlFile.getAbsolutePath}")

      if (!config.searchDirectory.exists)
        throw new IllegalArgumentException(s"Directory to search doesn't exist: ${config.searchDirectory.getAbsolutePath}")

      config

    case None =>
      throw new IllegalStateException(s"Failed to read configuration: ${args.mkString(", ")}")
  }
}
