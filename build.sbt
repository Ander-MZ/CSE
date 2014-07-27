import AssemblyKeys._

import NativePackagerKeys._

assemblySettings

jarName in assembly := "bagpipes_1.0.jar"

test in assembly := {}

name := "bagpipes_1.0"

scalaVersion := "2.10.4"

mainClass := Some("edu.cmu.lti.oaqa.bagpipes.run.BagPipesRun")

mainClass in assembly := Some("edu.cmu.lti.oaqa.bagpipes.run.BagPipesRun")

scalaSource in Compile := baseDirectory.value / "src"

packageArchetype.java_application
