package com.vividlab.rekordbox

import com.vividlab.rekordbox.analyse.Analyser
import com.vividlab.rekordbox.repair.Repairer
import org.slf4j.LoggerFactory

object Main {
  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    log.info(s"Running using Java version ${System.getProperty("java.version")}")

    val config = Config(args)

    Analyser.analyse(config) match {
      case Right(result) =>
        log.info(result.logText())

        Repairer.repair(config, result)

        FileUtils.writeReport(FileUtils.reportFileFromXmlFile(config.outputXmlFile), result)

        log.info("Completed successfully")
      case Left(e) =>
        log.error(s"Failed to complete successfully: ${e.getMessage}", e)
        System.exit(-1)
    }
  }
}
