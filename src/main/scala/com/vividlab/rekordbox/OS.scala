package com.vividlab.rekordbox

object OS {
  private val osName = System.getProperty("os.name").toLowerCase
  val isMac: Boolean = osName.startsWith("mac")
  val isWindows: Boolean = osName.startsWith("windows")

  val newLine: String = sys.props("line.separator")
  val paragraph: String = newLine + newLine
}
