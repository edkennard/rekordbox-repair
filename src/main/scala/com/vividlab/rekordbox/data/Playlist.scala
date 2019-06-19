package com.vividlab.rekordbox.data

import scala.xml.{Elem, Node, Text}

trait Playlist {
  def name: String

  def containsTrack(trackId: String): Boolean = {
    def recursiveSearch(pl: Playlist): Boolean = pl match {
      case fpl: FolderPlaylist =>
        fpl.children.exists(recursiveSearch)
      case tpl: TracksPlaylist =>
        tpl.tracks.exists(_.trackId == trackId)
    }
    recursiveSearch(this)
  }
}

object Playlist {
  def apply(node: Node): Playlist = node.attribute("Type") match {
    case Some(typ: Text) =>
      if (typ.text == "0")
        FolderPlaylist(node)
      else if (typ.text == "1")
        TracksPlaylist(node)
      else
        throw new IllegalArgumentException(s"Couldn't read a rekordbox playlist's XML, the Type attribute wasn't either 0 (folder) or 1 (tracks): $node")
    case _ =>
      throw new IllegalArgumentException(s"Couldn't read a rekordbox playlist's XML, it was missing the Type attribute: $node")
  }
}


case class FolderPlaylist(
  name: String,
  children: Seq[Playlist]
) extends Playlist

object FolderPlaylist {
  def apply(node: Node): FolderPlaylist = node.attribute("Name") match {
    case Some(name: Text) =>
      FolderPlaylist(name.text, (node \ "NODE").map(Playlist.apply))
    case _ =>
      throw new IllegalArgumentException(s"Couldn't read a rekordbox playlist's XML, it was missing the Name attribute: $node")
  }
}


case class TracksPlaylist(
  name: String,
  tracks: Seq[PlaylistTrack]
) extends Playlist

case object TracksPlaylist {
  def apply(node: Node): TracksPlaylist = node.attribute("Name") match {
    case Some(name) =>
      TracksPlaylist(name.text, PlaylistTracks.fromXml(node))
    case _ =>
      throw new IllegalArgumentException(s"Couldn't read a rekordbox playlist's XML, it was missing the Name attribute: $node")
  }
}


object RootPlaylist {
  def fromXml(xmlRoot: Elem): FolderPlaylist = (xmlRoot \ "PLAYLISTS" \ "NODE").headOption match {
    case Some(rootPlaylistNode) =>
      FolderPlaylist.apply(rootPlaylistNode)
    case None =>
      throw new IllegalArgumentException(s"Couldn't read the rekordbox root playlist XML")
  }
}
