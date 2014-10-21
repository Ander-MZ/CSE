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
import org.apache.spark.AccumulatorParam
import org.apache.spark.SparkContext._
import com.esotericsoftware.kryo._
import java.io.File
import edu.cmu.lti.oaqa.bagpipes.db.SqliteDB
import edu.cmu.lti.oaqa.bagpipes.db.BagpipesDatabase.Experiment
import edu.cmu.lti.oaqa.bagpipes.db.BagpipesDatabase
import java.sql.Timestamp
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob
import scala.util.Random

/*
 * 
 * @author: Ander Murillo (ander.murillo@itam.mx)
 */

object ParallelExecutorController extends java.io.Serializable {

  def apply[I](confSpaceStream: List[TreeWithHistory[AtomicExecutableConf]], exctr: Executor[I, _ <: ExecutableComponent[I]], confSpace: Root[CollectionReaderDescriptor, AtomicExecutableConf])(implicit scorer: Scorer[I] = DefaultScorer[I]) = {

    import ParallelExecutorController._

    def initializeCollectionReader(confRoot: Root[CollectionReaderDescriptor, AtomicExecutableConf]) = {
      val collectionReaderDesc = confRoot.getRoot
      exctr.getComponentFactory.createReader(collectionReaderDesc)
    }

    def execute(confSpace: Root[CollectionReaderDescriptor, AtomicExecutableConf]): Unit = {

      /*
     * Spark Context is created according to the specified arguments 
     */
      println("----------> Creating Spark Context")

      val masterUrl = System.getenv("MASTER")
      val projectName = "bagpipes"
      val sparkHome = System.getenv("SPARK_HOME")
      val jars = Array("/root/bagpipes/target/scala-2.10/bagpipes_fat_1.0.jar","/root/bagpipes/target/scala-2.10/bagpipes_1-0_2.10-0.0.1.jar")

      val conf = new SparkConf()
        .setMaster(masterUrl)
        .setAppName(projectName)
        .setSparkHome(sparkHome)
        .setJars(jars)

      val sc = new SparkContext(conf)
      
      type DBTrace = BagpipesDatabase.Trace
      
      //An implicit object for the accumulator variable
      implicit object SetACC extends AccumulatorParam[Set[DBTrace]]{
        def zero(s: Set[DBTrace]) = Set[DBTrace]()
        def addInPlace(s1: Set[DBTrace], s2: Set[DBTrace]) = s1 ++ s2
      }
      
      //Accumulator of traces, collects in parallel, stores in DB in master at the end.
      val tracesAcc = sc.accumulator(Set[DBTrace]())

      println("----------> Spark Context Created\n\n")    

      //Database creation and some auxiliary methods
      val db = new SqliteDB("jdbc:sqlite:traces.db")

      db.createTables
      db.insertExperiment(Experiment("UUID: " + System.currentTimeMillis, "bagpipes1", "AUTHOR", "CONFIG", Some("NOTES"), ts)) // ATTENTION: HARDCODED
      
      def ts: Timestamp = new java.sql.Timestamp(System.currentTimeMillis())
      
      def getRandomBlob(): Blob = {
        val b = Array[Byte](10)
        Random.nextBytes(b)
        new SerialBlob(b)
      }

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
        //Finished all inputs: accumulate executed traces and return final cache 
        case (List(), _, _) => {
          val traces = cache._2.dataCache.keySet
          val dbTraces = traces.map(t => BagpipesDatabase.Trace(t.hashCode, "TRACE", "UUID", getRandomBlob))
          tracesAcc += dbTraces
          //dbTraces.map(t => db.insertTrace(t)) //Can't be performed on parallel due DB being a single file
          cache
        }
        case ((compDesc @ TreeWithHistory(elem, hist)) :: rest, input, (_, cache)) =>
          val execResult = execute(compDesc, input)(cache)
          execStreamPar((rest, input))(execResult)
      }

      println("\nTree schema: " + confSpaceStream.toList.map(e => e.getClass().toString().split('.').last) + "\n")

      //Configuration space is cloned for each input
      val space = (1 to totalInputs).map((confSpaceStream, _))

      val parallelSpace = sc.parallelize(space)

      //Parallel mapping/exploration of the space
      val results = parallelSpace.map(execStreamPar(_))

      println("\n\nresults: " + results.count)
      
      println("\n\n----------> Start of ACC: " +  tracesAcc.value.size +" traces\n" + tracesAcc.value + "\n----------> End of ACC\n\n")
      
      println("\n\n----------> Saving traces into DB")
      
      tracesAcc.value.map(t => db.insertTrace(t))
      
      println("----------> Traces successfully saved\n\n")

      sc.stop

    }

    execute(confSpace)

  }
}
