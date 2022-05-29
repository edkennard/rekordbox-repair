package com.vividlab.rekordbox.repair

import com.vividlab.rekordbox.analyse.{LocateFileResult, LocateFileResults, Relocated}
import com.vividlab.rekordbox.data.Playlist
import com.vividlab.rekordbox.{Config, FileUtils}

import scala.language.{implicitConversions, reflectiveCalls}
import scala.xml._
import scala.xml.transform.RewriteRule

/**
  * Rewrites the rekordbox XML file to the configured output file, modifying the locations of any files that have been
  * relocated, and limiting the output to only the repaired tracks and their related playlists if configured to do so.
  *
  * There are some minor issues with Scala's XML library worth mentioning:
  *
  * - When re-writing XML attributes, there is a known bug where the ordering of the attributes gets reversed, so for
  *   example a collection track's TrackID attribute ends up being the last attribute at the end, rather than the first.
  *   While the XML is still valid and will be read by rekordbox without issue, it's harder to work with as a developer
  *   when you need to review the before-after delta.  There is already a fix for this merged into the master branch of
  *   the scala-xml project: https://github.com/scala/scala-xml/pull/172/files
  *
  * - Support for rewriting XML attributes could also be improved in the Scala XML library by providing better iteration
  *   over MetaData lists and a better Attribute.copy function. Given those points, note the helper functions included
  *   below.
  *
  * - The rewriter produces a lot of white space if sections of the input XML are being excluded from the output XML.
  *   This doesn't seem to bother rekordbox, but it did create some issues for the unit tests, see the test
  *   RelocatorTest... "End-to-end only outputting relocated tracks and their related playlists" for more info
  *
  * @param config Configuration specified by the user via the command line options
  * @param locateFileResults Results of the file relocate operation
  */
class RekordBoxRewriteRule(
  config: Config,
  locateFileResults: LocateFileResults
) extends RewriteRule {

  override def transform(node: Node): Seq[Node] = node match {
    case e: Elem if e.label == "COLLECTION" && config.outputRepairedTracksOnly =>
      // Rewrite collection track count if we're only outputting relocated tracks
      rewriteAttribute(e, "Entries", locateFileResults.relocated.size.toString)

    case e: Elem if e.label == "TRACK" =>

      def resultByTrackId(trackId: String): Option[LocateFileResult] =
        locateFileResults.results.find(_.track.id == trackId)

      // Match on either a collection track's TradeID attribute, or a playlist track's Key attribute
      (e.attribute("TrackID"), e.attribute("Key")) match {

        // Rewrite collection tracks with a new location attribute if necessary
        case (Some(trackId: Text), None) =>
          resultByTrackId(trackId.text).map {
            case relocated: Relocated =>
              rewriteAttribute(e, "Location", FileUtils.locationFromFile(relocated.newFile))
            case _ =>
              if (config.outputRepairedTracksOnly)
                Nil
              else
                node
          }.getOrElse(node)

        // Rewrite playlist tracks, limiting to only relocated tracks if requested
        case (None, Some(trackId)) if config.outputRepairedTracksOnly =>
          resultByTrackId(trackId.text).map {
            case _: Relocated => node
            case _ => Nil
          }.getOrElse(node)

        case _ => node
      }

    // Filter playlists if we're only outputting relocated tracks down to only playlists containing relocated tracks
    case e: Elem if e.label == "NODE" && config.outputRepairedTracksOnly =>
      if (locateFileResults.relocated.exists(result => Playlist(node).containsTrack(result.track.id)))
        node
      else
        Nil

    case _ => node
  }

  // The built-in copy function for attributes isn't sufficient so pimping it was necessary
  implicit def pimpedAttribute(attr: Attribute) = new {
    def pimpedCopy(key: String = attr.key, value: Any = attr.value): Attribute =
      Attribute(attr.pre, key, Text(value.toString), attr.next)
  }

  // Makes it possible to iterate over attributes using the preferred "for (attr <- e.attributes)" syntax
  implicit def pimpedIterableToMetaData(items: Iterable[MetaData]): MetaData = {
    items match {
      case Nil =>
        Null
      case head :: tail =>
        head.copy(next = pimpedIterableToMetaData(tail))
    }
  }

  private def rewriteAttribute(e: Elem, attributeName: String, newValue: String): Elem = {
    e.copy(attributes = e.attributes.map {
      case attr@Attribute(attrName, _, _) if attrName == attributeName =>
        attr.pimpedCopy(value = newValue)
      case other =>
        other
    })
  }
}
