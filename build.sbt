name := "TomLiddle"

version := "1.0"

scalaVersion := "2.11.7"

mainClass in Compile := Some("WebCrawler")
assemblyJarName in assembly := "WebCrawler.jar"

lazy val commonSettings = Seq(
	version := "0.1-SNAPSHOT",
	organization := "tomliddle",
	scalaVersion := "2.10.1"
)

lazy val app = (project in file("app")).
	settings(commonSettings: _*).
	settings(
		// your settings here
	)

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.5"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"
