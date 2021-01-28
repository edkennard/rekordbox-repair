package com.vividlab.rekordbox.analyse

import java.io.File
import java.nio.file.{Files, Paths}

import com.vividlab.rekordbox.{Config, FileUtils, OS}
import com.vividlab.rekordbox.data.{CollectionTrack, CollectionTracks, RootPlaylist}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.xml.XML

object Analyser {
  private val log = LoggerFactory.getLogger(getClass)

  def analyse(config: Config): Either[Exception, AnalyserResult] = {
    try {
      log.info("Analysing rekordbox collection...")

      log.info(s"Reading rekordbox XML file ${config.inputXmlFile.getAbsolutePath}...")
      val inputXml = XML.loadFile(config.inputXmlFile)

      log.info(s"Loading tracks in the collection...")
      val tracks = CollectionTracks.fromXml(inputXml)

      // Check here that all playlists can be successfully read now to avoid delaying failure until later when transforming the XML
      log.info(s"Checking playlists...")
      RootPlaylist.fromXml(inputXml)

      log.info(s"Locating files referenced by tracks in the collection...")
      val locateResults = locateFiles(tracks, config.searchDirectory)

      val allFilesInSearchDir = FileUtils.allSupportedFilesInDir(config.searchDirectory)

      log.info(s"Checking for files in the search directory which don't exist in rekordbox...")
      val notInRekordBox = filesNotInRekordBox(allFilesInSearchDir, locateResults.results)

      log.info(s"Checking for files with a path too long for rekordbox...")
      val tooLong = filesWithPathTooLong(allFilesInSearchDir)

      log.info("Finished analysing rekordbox collection")

      Right(AnalyserResult(locateResults, notInRekordBox, tooLong))
    } catch {
      case e: Exception =>
        Left(e)
    }
  }

  private def locateFiles(tracks: Seq[CollectionTrack], searchDirectory: File): LocateFileResults = {
    LocateFileResults(
      tracks.map(locateFile(_, searchDirectory))
    )
  }

  private def locateFile(track: CollectionTrack, searchDirectory: File): LocateFileResult = {
    val logPrefix = track.toString

    track.file match {
      case Some(file) =>
        if (file.exists()) {
          log.info(s"$logPrefix - OK")
          OK(track)
        } else {
          log.info(s"$logPrefix - MISSING, searching for file '${file.getName}'...")

          val searchResults = Files.walk(Paths.get(searchDirectory.toURI)).iterator().asScala.toSeq
            .map(_.toFile)
            .filter(_.getName == file.getName)

          if (searchResults.isEmpty) {
            log.warn(s"$logPrefix - Couldn't find '${file.getName}', track will remain in rekordbox as a missing file")
            Missing(track)
          } else if (searchResults.size == 1) {
            val relocated = Relocated(track, searchResults.head)
            log.info(s"$logPrefix - Found one matching filename, relocating in rekordbox to '${searchResults.head.getCanonicalPath}'")
            relocated
          } else {
            val multiple = MultipleLocations(track, searchResults)
            log.warn(s"$logPrefix - Found more than one matching filename so it's not safe to automatically relocate. Matches were:${OS.newLine}${searchResults.map(_.getCanonicalPath).mkString(OS.newLine)}")
            multiple
          }
        }
      case None =>
        Invalid(track)
    }
  }

  /**
    * Builds a list of the files which are in some way already referenced by rekordbox, either by being a track in the
    * original collection, or a track that is in the process of being repaired by this tool or has been flagged as having
    * multiple potential new locations
    */
  private def filesNotInRekordBox(allFilesInSearchDir: Seq[File], locateResults: Seq[LocateFileResult]): Seq[File] = {

    def filesFromResult(result: LocateFileResult): Seq[File] = {
      result match {
        case r: Relocated =>
          Seq(r.newFile)
        case m: MultipleLocations =>
          m.newFiles
        case _ =>
          // Not interested in the other result types, e.g. OK / Missing
          Seq()
      }
    }
    val collectionFiles = locateResults.flatMap(_.track.file)
    val locateResultFiles = locateResults.flatMap(result => filesFromResult(result))

    val notInRekordBox = allFilesInSearchDir.filterNot((collectionFiles ++ locateResultFiles).contains)

    if (notInRekordBox.nonEmpty)
      log.info(s"${notInRekordBox.size} files in the search directory don't exist in rekordbox:${OS.newLine}${notInRekordBox.mkString(OS.newLine)}")

    notInRekordBox
  }

  /**
    * rekordbox has a maximum file path of 255 characters, which can easily be exceeded by a rogue album or track
    * name.  Any files on disk exceeding this limit will never make it into rekordbox
    */
  private def filesWithPathTooLong(allFilesInSearchDir: Seq[File]): Seq[File] = {
    val tooLong = allFilesInSearchDir.filter(_.getAbsolutePath.length > 256)

    if (tooLong.nonEmpty)
      log.info(s"${tooLong.size} file paths are too long for rekordbox (255 character limit):${OS.newLine}${tooLong.mkString(OS.newLine)}")

    tooLong
  }
}
