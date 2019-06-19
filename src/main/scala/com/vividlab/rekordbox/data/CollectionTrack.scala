package com.vividlab.rekordbox.data

import java.io.File

import com.vividlab.rekordbox.FileUtils

import scala.xml.{Elem, Node}

case class CollectionTrack(
  id: String,
  artist: String,
  album: String,
  name: String,
  location: String
) {
  override def toString: String = {
    val artistName = if (artist.length > 0) s"'$artist'" else "No Artist"
    val albumName = if (album.length > 0) s"'$album'" else "No Album"
    s"$artistName - $albumName - '$name'"
  }

  val file: File = FileUtils.fileFromLocation(location)
  val filePath: String = file.getCanonicalPath
}

object CollectionTrack {
  def apply(node: Node): CollectionTrack = {
    (node.attribute("TrackID"), node.attribute("Artist"), node.attribute("Album"), node.attribute("Name"), node.attribute("Location")) match {
      case (Some(id), Some(artist), Some(album), Some(name), Some(location)) =>
        CollectionTrack(id.text, artist.text, album.text, name.text, location.text)
      case _ =>
        throw new IllegalArgumentException(s"Couldn't read a rekordbox track's XML, it was missing either a TrackID, Name, Artist or Location attribute: $node")
    }
  }
}

object CollectionTracks {
  def fromXml(xmlRoot: Elem): Seq[CollectionTrack] = xmlRoot \ "COLLECTION" \ "TRACK" map { CollectionTrack.apply }
}
