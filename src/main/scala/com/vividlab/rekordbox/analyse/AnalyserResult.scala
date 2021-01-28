package com.vividlab.rekordbox.analyse

import java.io.File

import com.vividlab.rekordbox.OS

import scala.io.Source

case class AnalyserResult(
  locateFileResults: LocateFileResults,
  filesNotInRekordBox: Seq[File],
  filesWithPathTooLong: Seq[File]
) {
  val total: Int = locateFileResults.results.size

  val summary: String = s"""
    |Total tracks in collection: $total
    |Tracks OK: ${locateFileResults.ok.size}
    |Tracks repaired: ${locateFileResults.relocated.size}
    |Tracks with multiple matches: ${locateFileResults.multipleLocations.size}
    |Tracks with missing files: ${locateFileResults.missing.size}
    |Tracks with invalid paths: ${locateFileResults.invalid.size}
    |Tracks with path too long: ${filesWithPathTooLong.size}
    |Tracks on disk but not in rekordbox: ${filesNotInRekordBox.size}
    |""".stripMargin

  def logText(): String =
    s"${OS.newLine}************* Results Summary *************$summary*******************************************"


  def reportText(): String =
    Source.fromResource(s"report-template.txt").getLines().mkString(OS.newLine)
      .replace("{summary}", summary)
      .replace("{tracks-repaired}", locateFileResults.relocated.map(_.toString).mkString(OS.newLine))
      .replace("{tracks-multiple-matches}", locateFileResults.multipleLocations.map(_.toString).mkString(OS.newLine))
      .replace("{tracks-missing}", locateFileResults.missing.map(_.toString).mkString(OS.newLine))
      .replace("{tracks-invalid-path}", locateFileResults.invalid.map(_.toString).mkString(OS.newLine))
      .replace("{tracks-path-too-long}", filesWithPathTooLong.map(_.getAbsolutePath).mkString(OS.newLine))
      .replace("{tracks-not-in-rekordbox}", filesNotInRekordBox.map(_.getAbsolutePath).mkString(OS.newLine))
}
