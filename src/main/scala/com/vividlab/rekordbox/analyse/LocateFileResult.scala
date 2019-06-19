package com.vividlab.rekordbox.analyse

import java.io.File

import com.vividlab.rekordbox.OS
import com.vividlab.rekordbox.data.CollectionTrack

import scala.collection.breakOut
import scala.reflect.ClassTag

sealed trait LocateFileResult {
  def track: CollectionTrack
}

case class OK(track: CollectionTrack) extends LocateFileResult

case class Missing(track: CollectionTrack) extends LocateFileResult {
  override def toString: String = {
    s"$track${OS.newLine}Missing: ${track.filePath}${OS.newLine}"
  }
}

case class Relocated(track: CollectionTrack, newFile: File) extends LocateFileResult {
  override def toString: String = {
    s"$track${OS.newLine}Old: ${track.filePath}${OS.newLine}New: ${newFile.getAbsolutePath}${OS.newLine}"
  }
}

case class MultipleLocations(track: CollectionTrack, newFiles: Seq[File]) extends LocateFileResult {
  override def toString: String = {
    s"$track${OS.newLine}Old:${OS.newLine}${track.filePath}${OS.paragraph}New (Potentials):${OS.newLine}${newFiles.map(_.getAbsolutePath).mkString(OS.newLine)}"
  }
}

case class LocateFileResults(results: Seq[LocateFileResult]) {

  implicit class RichTraversable[A](collection: Traversable[A]) {
    def filterOnType[T <: A](implicit t: ClassTag[T]): Vector[T] = {
      collection.filter(a => t.runtimeClass.isAssignableFrom(a.getClass)).map(_.asInstanceOf[T])(breakOut)
    }
  }

  val ok: Seq[OK] = results.filterOnType[OK].sortBy(_.track.artist.toLowerCase)
  val relocated: Seq[Relocated] = results.filterOnType[Relocated].sortBy(_.track.artist.toLowerCase)
  val multipleLocations: Seq[MultipleLocations] = results.filterOnType[MultipleLocations].sortBy(_.track.artist.toLowerCase)
  val missing: Seq[LocateFileResult] = results.filterOnType[Missing].sortBy(_.track.artist.toLowerCase)
}
