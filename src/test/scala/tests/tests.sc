package tests

import breeze.stats.distributions


object tests {
  
	val m1 = Map(1->'a', 2 -> 'b')            //> m1  : scala.collection.immutable.Map[Int,Char] = Map(1 -> a, 2 -> b)
	
  val m2 = m1.updated(2 , 'c')                    //> m2  : scala.collection.immutable.Map[Int,Char] = Map(1 -> a, 2 -> c)
	
}