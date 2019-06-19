package com.vividlab.rekordbox.data

import scala.xml.Node

case class PlaylistTrack(
  trackId: String
)

object PlaylistTrack {
  def apply(node: Node): PlaylistTrack = node.attribute("Key") match {
    case Some(trackId) =>
      PlaylistTrack(trackId.text)
    case _ =>
      throw new IllegalArgumentException(s"Couldn't read a playlist track's XML, it was missing the 'Key' attribute: $node")
  }
}

object PlaylistTracks {
  def fromXml(playlistNode: Node): Seq[PlaylistTrack] = playlistNode \ "TRACK" map { PlaylistTrack.apply }
}
