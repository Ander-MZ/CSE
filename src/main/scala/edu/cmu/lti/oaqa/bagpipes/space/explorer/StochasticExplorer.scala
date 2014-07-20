package edu.cmu.lti.oaqa.bagpipes.space.explorer

import StochasticExplorer._
import edu.cmu.lti.oaqa.bagpipes.space.ConfigurationSpace._
import edu.cmu.lti.oaqa.bagpipes.configuration.AbstractDescriptors._
import edu.cmu.lti.oaqa.bagpipes.configuration.Descriptors.CollectionReaderDescriptor
import edu.cmu.lti.oaqa.bagpipes.space.Leaf
import edu.cmu.lti.oaqa.bagpipes.space.Root
import edu.cmu.lti.oaqa.bagpipes.space.Node
import edu.cmu.lti.oaqa.bagpipes.space.TreeWithChildren
import scala.collection.immutable.Stream.consWrapper

import breeze.stats.distributions

/*
 * 
 * @author: Ander Murillo (ander.murillo@itam.mx)
 */

class StochasticExplorer[R <: T, T, I](order: DistType) extends Explorer[R, T, I]{
  
  val dist : Distribution  = order match {
    
  case (GaussianDist|_) => Gaussian(1.0,1.0)
    
  }
  
  def this() = this(???)

  override def from(initial: Stream[ExecutableTree])(implicit input: I): Stream[ExecutableTree] = fromDist(initial)(dist)

  
  def fromDist(initial: Stream[ExecutableTree])(dist: Distribution)(implicit input: I): Stream[ExecutableTree] = {

    //println("Before: " + formatTree(initial) + "\n")
    
    /*
     * Takes a stream of components and returns a random sample of
     * them, according to a condition involving the distribution 'dist'
     */
    def sample(current: Stream[ExecutableTree]): Stream[ExecutableTree] = current match {
      case Stream() => Stream()
      case _ => {
	    if (dist.draw < dist.mean + 10) current.head #:: sample(current.tail)
	    else sample(current.tail)
      }
    }
    def fromDistribution(initial: Stream[ExecutableTree]): Stream[ExecutableTree] = initial match {
      case Stream() => Stream() // no more nodes, terminate
      case (current @ Leaf(_, _)) #:: siblings => current #:: fromDistribution(siblings) // leaf encountered, visit current, and go to next sibling  
      case (current @ TreeWithChildren(_, children, _)) #:: siblings => // node encountered, 
      current #:: fromDistribution(sample(children)) #::: fromDistribution(siblings) //visit current, then visit its children, and then visit its siblings
    }  
    fromDistribution(initial)
    
  }
  
  /*
   * Method that updates the map of weights of a given trace. The map has
   * the following structure:
   * 
   * Key: Hash Code , Value: (Random Number, Cumulative Weight)
   */
  def weighTrace(trace: Stream[ExecutableTree])(weights: Map[Int,(Double,Double)]): Map[Int,(Double,Double)] = trace match{
    case Stream() => Map()
    case comp @ _ => Map()
  } 
  
  
  /*
   * Method that takes a space configuration tree and returns a stream
   * containing all its individual traces 
   */
  def splitTree(initial: Stream[ExecutableTree]): Stream[ExecutableTree] = {
    Stream()
  }
   
  
  private def formatTree(tree: Stream[_]): String = tree match {
      case Stream() => "" //Empty stream
      case elem #:: next => { //Stream
        val s = elem match{
          case r @ Root(_,children) => "" + formatTree(children)
          case n @ Node(_,children,_) => "[Node]"  + " <" +n.stdHashCode + "> " + formatTree(children)
          case l @ Leaf(_,_) => "Leaf" + " <" + l.stdHashCode + "> "
        }
        s + formatTree(next)
      }
    }

}

  /*
   * A Distribution trait to allow the explorer to receive an object
   * previously initialized, which has the 3 basic operations any 
   * probability distribution needs: draw a value, get the mean and
   * get the variance. 
   * 
   * This container also allows all parameters to be normalized
   * into Double types, regardless of the used distribution
   * 
   */

sealed abstract trait Distribution{
    def draw: Double
    def mean: Double
    def variance: Double
  }
  
  case class Uniform(a: Double, b: Double) extends Distribution{
    override def draw: Double = distributions.Uniform(a,b).draw
    override def mean: Double = (a + b) / 2.0
    override def variance: Double = math.pow((a+b), 2) / 12.0
  }
  case class Gaussian(m: Double, v: Double) extends Distribution{
    override def draw: Double = distributions.Gaussian(m,v).draw
    override def mean: Double = m
    override def variance: Double = v
  }
  case class Gamma(shape: Double, scale: Double) extends Distribution{
    override def draw: Double = distributions.Gamma(shape,scale).draw
    override def mean: Double = shape*scale
    override def variance: Double = shape*scale*scale
  }
  case class Poisson(m: Double) extends Distribution{
    override def draw: Double = (distributions.Poisson(m).draw).toDouble
    override def mean: Double = m
    override def variance: Double = m
  }
  case class Binomial(n: Int, p: Double) extends Distribution{
    override def draw: Double = (distributions.Binomial(n,p).draw).toDouble
    override def mean: Double = n*p
    override def variance: Double = n*p*(1-p)
  }


/*
 * Explorer singletons
 */
object UniformExplorer extends StochasticExplorer[CollectionReaderDescriptor, AtomicExecutableConf,Int](StochasticExplorer.UniformDist)
object GaussExplorer extends StochasticExplorer[CollectionReaderDescriptor, AtomicExecutableConf,Int](StochasticExplorer.GaussianDist)
object GammaExplorer extends StochasticExplorer[CollectionReaderDescriptor, AtomicExecutableConf,Int](StochasticExplorer.GammaDist)
object PoissonExplorer extends StochasticExplorer[CollectionReaderDescriptor, AtomicExecutableConf,Int](StochasticExplorer.PoissonDist)
object BinomialExplorer extends StochasticExplorer[CollectionReaderDescriptor, AtomicExecutableConf,Int](StochasticExplorer.BinomialDist)

/*
 * Enums for selecting the type of stochastic sampling that will be
 * used for the exploration
 */
object StochasticExplorer {
  sealed trait DistType
  object UniformDist extends DistType
  object GaussianDist extends DistType 
  object GammaDist extends DistType
  object PoissonDist extends DistType
  object BinomialDist extends DistType
}