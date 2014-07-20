package edu.cmu.lti.oaqa.bagpipes.run
import edu.cmu.lti.oaqa.bagpipes.space.explorer.Explorer
import edu.cmu.lti.oaqa.bagpipes.space.explorer.SimpleExplorer._
import edu.cmu.lti.oaqa.bagpipes.configuration.Descriptors.ConfigurationDescriptor
import edu.cmu.lti.oaqa.bagpipes.space.ConfigurationSpace
import edu.cmu.lti.oaqa.bagpipes.configuration.Descriptors.CollectionReaderDescriptor
import edu.cmu.lti.oaqa.bagpipes.configuration.AbstractDescriptors._
import edu.cmu.lti.oaqa.bagpipes.controller.ExecutionController
import edu.cmu.lti.oaqa.bagpipes.controller.ParallelExecutorController
import edu.cmu.lti.oaqa.bagpipes.executor.uima.UimaExecutor
import edu.cmu.lti.oaqa.bagpipes.space.explorer.DepthExplorer
import edu.cmu.lti.oaqa.bagpipes.space.explorer.BreadthExplorer
import edu.cmu.lti.oaqa.bagpipes.space.explorer.KBestPathExplorer
import edu.cmu.lti.oaqa.bagpipes.configuration.YAMLParser
import edu.cmu.lti.oaqa.bagpipes.space.explorer.UniformExplorer

import edu.cmu.lti.oaqa.bagpipes.space.explorer.Distribution
import edu.cmu.lti.oaqa.bagpipes.space.explorer._

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object BagPipesRun extends App{
  //pass some of these as arguments or in the yaml descriptor
  //Also, make explorer and executor parameters

  def run(descPath: String, baseDir: Option[String] = None, fromFile: Boolean = true): Unit = {
    val controller = SimpleUimaExecutionController
   // val parallelController = SparkExecutionController
    val parser = YAMLParser(baseDir)
    //parse to ConfigurationDescriptor object
    val confDesc = parser.parse(descPath, fromFile)
    //ConfigurationDescriptor -> ConfigurationSpace
    val spaceTree = ConfigurationSpace(confDesc).getSpace

    

    val masterUrl = "local[4]"
    val projectName = "Bagpipes"
    val sparkHome = "/Users/andemurillo/Development/Spark"
    val sparkArgs = Array(masterUrl,projectName,sparkHome)

    //execute pipeline
    //controller.execute(spaceTree)
   //parallelController.execute(spaceTree,sparkArgs)
    val confSpaceStream = GaussExplorer.fromRoot(spaceTree)(0)
    
    ParallelExecutorController(confSpaceStream.toList, UimaExecutor,spaceTree)

  }

  override def main(args: Array[String]) = {
    println("Main @BagPipesRun.scala")
    val baseDir = "src/test/resources"
    run(args(0), Some(baseDir), true)
  }

}

object SimpleUimaExecutionController extends ExecutionController(GaussExplorer, UimaExecutor)
//object SparkExecutionController extends ParallelExecutorController(GaussExplorer, UimaExecutor)
