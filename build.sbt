name := "Eratosthenes"

version := "1.0"

scalaVersion := "2.9.2"

scalacOptions ++= Seq("-unchecked", "-deprecation")

initialCommands in console := "import eratosthenes._"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"
