import AssemblyKeys._

import NativePackagerKeys._

assemblySettings

jarName in assembly := "bagpipes_1.0.jar"

test in assembly := {}

name := "bagpipes_1.0"

scalaVersion := "2.10.4"

mainClass := Some("edu.cmu.lti.oaqa.bagpipes.cmd.CmdParser")

mainClass in assembly := Some("edu.cmu.lti.oaqa.bagpipes.run.BagPipesRun")

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalacOptions += "-target:jvm-1.6"

scalaSource in Compile := baseDirectory.value / "src"

//resourceDirectory in Compile := baseDirectory.value / "src"
//managedSourceDirectories in Compile += baseDirectory.value / "src/test/"

//managedResourceDirectories in Compile += baseDirectory.value / "src/test/resources/"

//resources += file("./src/test/resources")

packageArchetype.java_application

net.virtualvoid.sbt.graph.Plugin.graphSettings
//scriptClasspath += baseDirectory.value + "src/test/resources"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case m if m.toLowerCase.matches("meta-inf/.*\\.sf$") => MergeStrategy.discard
   case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case _ => MergeStrategy.last}
/* {
    case PathList("META-INF", xs @ _*) =>
      (xs map {_.toLowerCase}) match {
        case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
          MergeStrategy.discard
          case ps @ (x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
          MergeStrategy.discard
          case "plexus" :: xs =>
          MergeStrategy.discard
          case "services" :: xs =>
          MergeStrategy.filterDistinctLines
          case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
          MergeStrategy.filterDistinctLines
          case _ => MergeStrategy.deduplicate
      }
    case PathList("org","eclipse","jetty","orbit", tail @ _*) => MergeStrategy.last
    case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
    case PathList("javax", "mail", xs @ _*) => MergeStrategy.last
    case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
    case PathList("org", "apache", xs @ _*) => MergeStrategy.last
    case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
    case "about.html" => MergeStrategy.rename
    case x => old(x)
  } */
} 
