package com.vividlab.rekordbox.repair

import com.vividlab.rekordbox.Config
import com.vividlab.rekordbox.analyse.AnalyserResult
import org.slf4j.LoggerFactory

import scala.xml.XML
import scala.xml.transform.RuleTransformer

object Repairer {
  private val log = LoggerFactory.getLogger(getClass)

  def repair(config: Config, analyserResult: AnalyserResult): Unit = {
    log.info("Generating repaired rekordbox collection...")

    val inputXml = XML.loadFile(config.inputXmlFile)

    log.info(s"Transforming XML...")
    val rule = new RekordBoxRewriteRule(config, analyserResult.locateFileResults)
    val outputXml = new RuleTransformer(rule).transform(inputXml)

    log.info(s"Saving new XML to ${config.outputXmlFile.getAbsolutePath}...")
    XML.save(config.outputXmlFile.getAbsolutePath, outputXml.head, "UTF-8", xmlDecl = true)

    log.info("Finished generating repaired rekordbox collection")
  }
}
