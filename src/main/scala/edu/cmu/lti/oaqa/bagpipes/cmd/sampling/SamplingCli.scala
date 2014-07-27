package edu.cmu.lti.oaqa.bagpipes.cmd.sampling

import scala.collection.JavaConversions._
import scala.collection.immutable.Stream.consWrapper
import edu.cmu.lti.oaqa.bagpipes.configuration.AbstractDescriptors._
import edu.cmu.lti.oaqa.bagpipes.configuration.AbstractDescriptors.AtomicExecutableConf
import edu.cmu.lti.oaqa.bagpipes.configuration.Descriptors._
import edu.cmu.lti.oaqa.bagpipes.configuration.YAMLParser
import edu.cmu.lti.oaqa.bagpipes.configuration.YAMLModParser
import edu.cmu.lti.oaqa.bagpipes.space.Child
import edu.cmu.lti.oaqa.bagpipes.space.ConfigurationSpace
import edu.cmu.lti.oaqa.bagpipes.space.ConfigurationSpace._
import edu.cmu.lti.oaqa.bagpipes.space.TreeWithChildren
import edu.cmu.lti.oaqa.bagpipes.space.TreeWithHistory
import edu.cmu.lti.oaqa.bagpipes.space.Leaf
import edu.cmu.lti.oaqa.bagpipes.run.BagPipesRun


object SamplingCli {
  
  def main(args: Array[String]): Unit = {
    

    //BouncingExploration.main(Array[String]())
    
    //val be = new BouncingExploration()
    
    //be.aMethod("ss")

    //val parser = YAMLModParser(Some(args(0)))
    //val configuration = parser.parse(args(1), true)
    
    //val pipeline = configuration.pipeline
    //val space = ConfigurationSpace(configuration)
    //val ss = space.getSpace.getChildren

  }

}