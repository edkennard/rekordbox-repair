package com.vividlab.rekordbox

import java.io.File

import scala.xml.{PrettyPrinter, XML}

trait TestUtils {

  def xmlFilesMatch(file1: File, file2: File): Boolean = {
    val printer = new PrettyPrinter(200, 2)

    XML.loadString(printer.format(XML.loadFile(file1)))
      .equals(XML.loadString(printer.format(XML.loadFile(file2))))
  }
}
