package edu.cmu.lti.oaqa.bagpipes.controller

import edu.cmu.lti.oaqa.bagpipes.space.explorer.Explorer
import edu.cmu.lti.oaqa.bagpipes.space.explorer.Distribution
import edu.cmu.lti.oaqa.bagpipes.space.explorer._
import edu.cmu.lti.oaqa.bagpipes.space._
import edu.cmu.lti.oaqa.bagpipes.executor.Executor
import edu.cmu.lti.oaqa.bagpipes.executor.ExecutableComponent
import edu.cmu.lti.oaqa.bagpipes.executor.Result
import edu.cmu.lti.oaqa.bagpipes.configuration.Descriptors._
import edu.cmu.lti.oaqa.bagpipes.configuration.AbstractDescriptors._
import edu.cmu.lti.oaqa.bagpipes.scorer.Scorer
import edu.cmu.lti.oaqa.bagpipes.scorer.DefaultScorer
import scala.collection.immutable.Stream.consWrapper
import edu.cmu.lti.oaqa.bagpipes.executor.Trace
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import com.esotericsoftware.kryo._
import java.io.File

/*
 * 
 * @author: Ander Murillo (ander.murillo@itam.mx)
 */
/*
class ParallelExecutorController[I](explr: Explorer[CollectionReaderDescriptor, AtomicExecutableConf, Int],
									exctr: Executor[_, _ <: ExecutableComponent[_]])
						 (implicit scorer: Scorer[_] = DefaultScorer[I]) {

  import ParallelExecutorController._

  def initializeCollectionReader(confRoot: Root[CollectionReaderDescriptor, AtomicExecutableConf]) = {
    val collectionReaderDesc = confRoot.getRoot
    exctr.getComponentFactory.createReader(collectionReaderDesc)
  }
  

  def execute(confSpace: Root[CollectionReaderDescriptor, AtomicExecutableConf], args: Array[String]): Unit = {

    /*
     * Spark Context is created according to the specified arguments 
     */   
    println("----------> Creating Spark Context")
    
    val conf = new SparkConf()
      .setMaster(args(0))
      .setAppName(args(1))
      .setSparkHome(args(2))

    val sc = new SparkContext(conf)
    
    println("----------> Spark Context Created\n\n")

    val collectionReader = initializeCollectionReader(confSpace)
    val totalInputs = collectionReader.getTotalInputs
    implicit val input = 0
    
    /*
     * The space gets explored with the specified technique
     */
    lazy val confSpaceStream = explr.fromRoot(confSpace)(input)

    /*
     * Method that executes a component
     */
    def execute(compDesc: TreeWithHistory[AtomicExecutableConf], input: Int)(implicit cache: exctr.Cache) = compDesc match {
      case (compDesc @ TreeWithHistory(elem, hist)) =>
        println("Executing component: " + compDesc.getClass() + " with input: " + input)
        val result @ (Result(res), _) = exctr.execute(elem, Trace(input, hist))(cache)
        println("result:  " + res)
        result
    }

    type ExecutionResult = (Result[_], exctr.Cache)
    type ExecutionInput = (Stream[TreeWithHistory[AtomicExecutableConf]], Int) // (ConfSpace, Input #)
    type ParallelSpace = Stream[ExecutionInput]
    
    /*
     * A base, empty container for results that will be updated throughout the execution
     */
    def getBlankResult(inputNum: Int)(implicit cache: exctr.Cache = exctr.getEmptyCache(inputNum)) =
      (exctr.getFirstInput, cache ++ exctr.getEmptyCache(inputNum))
      
    
    /*
     * This method receives an ExecutionInput (a tuple containing a configuration space
     * and an input number) and returns an ExecutionResult (a tuple containing the
     * result of executing the space and its cache). 
     */
    def execStreamPar(execInput: ExecutionInput)(implicit cache: ExecutionResult = getBlankResult(execInput._2)): ExecutionResult = (execInput._1, execInput._2, cache) match {
       //Finished all inputs return final cache
      case (Stream(), _, _) => cache
       
      case ((compDesc @ TreeWithHistory(elem, hist)) #:: rest, input, (_, cache)) =>
        val execResult = execute(compDesc, input)(cache)
        execStreamPar((rest, input))(execResult)
    }
    
    
    /*
     * This methods creates a Stream of ExecutionInput objects (tuple of (space, input) for each input)
     */
    def createInputs(tree: Stream[TreeWithHistory[AtomicExecutableConf]]): ParallelSpace = {
      println("total inputs: " + totalInputs)
      val res = for{
        i <- 1 to totalInputs
      } yield (tree,i)
      res.toStream
    }
    

    //val res = execStream(confSpaceStream, totalInputs - 1)
    
    println("\nTotal components: " + confSpaceStream.length + "\n")
    
    val space = createInputs(confSpaceStream)
    
    //val results = space.toList.map(execStreamPar(_))
    
    val parallelSpace = sc.parallelize(space)
    
    val results = parallelSpace.map(s => execStreamPar(s))
    
    println("\n\nresults: " + results.count)
    

    
    
  }
 

  
}
*/
object ParallelExecutorController extends java.io.Serializable{
 // def apply[I](explr: Explorer[CollectionReaderDescriptor, AtomicExecutableConf, Int], exctr: Executor[I, _ <: ExecutableComponent[I]], confSpace:Root[CollectionReaderDescriptor, AtomicExecutableConf])(implicit scorer: Scorer[I] = DefaultScorer[I]) = {
  def apply[I](confSpaceStream: List[TreeWithHistory[AtomicExecutableConf]], exctr: Executor[I, _ <: ExecutableComponent[I]], confSpace:Root[CollectionReaderDescriptor, AtomicExecutableConf])(implicit scorer: Scorer[I] = DefaultScorer[I]) = {

  import ParallelExecutorController._

  def initializeCollectionReader(confRoot: Root[CollectionReaderDescriptor, AtomicExecutableConf]) = {
    val collectionReaderDesc = confRoot.getRoot
    exctr.getComponentFactory.createReader(collectionReaderDesc)
  }
  

  def execute(confSpace: Root[CollectionReaderDescriptor, AtomicExecutableConf], args: Array[String]): Unit = {

    /*
     * Spark Context is created according to the specified arguments 
     */   
    println("----------> Creating Spark Context")
    
    val conf = new SparkConf()
      .setMaster(args(0))
      .setAppName(args(1))
      .setSparkHome(args(2))
      .setJars(args(3).split(" ").toList)

    val sc = new SparkContext(conf)
    
    println("----------> Spark Context Created\n\n")

    val collectionReader = initializeCollectionReader(confSpace)
    val totalInputs = collectionReader.getTotalInputs


    /*
     * Method that executes a component
     */
    def execute(compDesc: TreeWithHistory[AtomicExecutableConf], input: Int)(implicit cache: exctr.Cache) = compDesc match {
      case (compDesc @ TreeWithHistory(elem, hist)) =>
        println("Executing component: " + compDesc.getClass() + " with input: " + input)
        val result @ (Result(res), _) = exctr.execute(elem, Trace(input, hist))(cache)
        println("result:  " + res)
        result
    }

    type ExecutionResult = (Result[_], exctr.Cache)
    type ExecutionInput = (List[TreeWithHistory[AtomicExecutableConf]], Int) // (ConfSpace, Input #)
    
    /*
     * A base, empty container for results that will be updated throughout the execution
     */
    def getBlankResult(inputNum: Int)(implicit cache: exctr.Cache = exctr.getEmptyCache(inputNum)) =
      (exctr.getFirstInput, cache ++ exctr.getEmptyCache(inputNum))
      
    
    /*
     * This method receives an ExecutionInput (a tuple containing a configuration space
     * and an input number) and returns an ExecutionResult (a tuple containing the
     * result of executing the space and its cache). 
     */
    def execStreamPar(execInput: ExecutionInput)(implicit cache: ExecutionResult = getBlankResult(execInput._2)): ExecutionResult = (execInput._1, execInput._2, cache) match {
       //Finished all inputs return final cache
      case (List(), _, _) => cache
       
      case ((compDesc @ TreeWithHistory(elem, hist)) :: rest, input, (_, cache)) =>
        val execResult = execute(compDesc, input)(cache)
        execStreamPar((rest, input))(execResult)
    }
    
    //val res = execStream(confSpaceStream, totalInputs - 1)
    
    println("\nTotal components: " + confSpaceStream.length + "\n")
    
    val space = (1 to totalInputs).map((confSpaceStream,_))
    
    val parallelSpace = sc.parallelize(space)
    
    val results = parallelSpace.map(execStreamPar(_))
    
    println("\n\nresults: " + results.count)
    
      
   sc.stop   
  }
  
    val masterUrl = System.getenv("MASTER") //+ "[4]" //"local[4]"
    val projectName = "bagpipes"
    val sparkHome = System.getenv("SPARK_HOME") 
    val jars = "/root/bagpipes/target/scala-2.10/bagpipes_1.0.jar /root/bagpipes/target/scala-2.10/bagpipes_1-0_2.10-0.0.1.jar"
    val sparkArgs = Array(masterUrl,projectName,sparkHome,jars)


   execute(confSpace,sparkArgs) 
    
   //new ParallelExecutorController(explr, exctr)
  }
}
