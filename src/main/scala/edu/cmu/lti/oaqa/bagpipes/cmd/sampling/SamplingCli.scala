package edu.cmu.lti.oaqa.bagpipes.cmd.sampling

import scala.collection.JavaConversions._
import scala.collection.immutable.Stream.consWrapper
import bouncingExploration.BouncingExploration
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


object SamplingCli {
  
  var items = Array(0,0,0,0)

  private def fromDepth[T](initial: Stream[TreeWithHistory[T]], d : Int): List[T] = initial match {
    case Stream() => List()
    
    case (current @ Leaf(_, _)) #:: siblings => 
      println("Current Level: " + d + " : " + current.elem);
      items(d)=items(d)+1;
      current.elem :: fromDepth(siblings, d);
    
    case (current @ TreeWithChildren(elem, children, _)) #:: siblings =>
      println("Current Level: " + d + " : " + current.getElem);
      items(d)=items(d)+1;
      elem :: fromDepth(children, d+1) ::: fromDepth(siblings,d); 
  }
  
  private def obtainTree[_](initial: Stream[TreeWithHistory[_]], d : Int): List[_] = initial match {
    case Stream() => List()
    
    case (current @ Leaf(_, _)) #:: siblings => 
      //println("Current Level: " + d + " : " + current.elem);
      items(d)=items(d)+1;
      current.elem :: obtainTree(siblings, d);
    
    case (current @ TreeWithChildren(elem, children, _)) #:: siblings =>
      //println("Current Level: " + d + " : " + current.getElem);
      items(d)=items(d)+1;
      elem :: List(obtainTree(children, d+1)) ::: obtainTree(siblings,d); 
  }
  
  private def printTree[T](element : T) : List[_] = element match {
    case (current @ List(_,_)) => 
      println("List");
      printTree(current.drop(1));
    case _ => 
      println("Empty");
      List();
  }


  def main(args: Array[String]): Unit = {

    //BouncingExploration.main(Array[String]())
    
    //val be = new BouncingExploration()
    
    //be.aMethod("ss")

    val parser = YAMLModParser(Some(args(0)))
    val configuration = parser.parse(args(1), true)
    
    println("\n\n\n")
    
    val pipeline = configuration.pipeline
    val space = ConfigurationSpace(configuration)
    val ss = space.getSpace.getChildren
    
    val tree = obtainTree(ss, d=0)

    
    println("\n\n\n")
    
    println(s"---> Tree: ${tree}")
    
    println("\n\n\n")
    
    
    items foreach {println _}
    
    println("\n\n\n")
    
    val test1 = List(List(1,List(1,2)),List(2,List(1)))
    val test2 = "Not a list"
    
    println(printTree(test1))
       


  }

}