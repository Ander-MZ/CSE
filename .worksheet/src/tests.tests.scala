package tests

import breeze.stats.distributions


object tests {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(100); 
  
	val m1 = Map(1->'a', 2 -> 'b');System.out.println("""m1  : scala.collection.immutable.Map[Int,Char] = """ + $show(m1 ));$skip(33); 
	
  val m2 = m1.updated(2 , 'c');System.out.println("""m2  : scala.collection.immutable.Map[Int,Char] = """ + $show(m2 ))}
	
}
