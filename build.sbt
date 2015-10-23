name := "reactive-hbase"

version := "1.0"


scalaVersion := "2.11.6"

lazy val versionOfAkka = "2.3.11"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "com.typesafe.akka" %% "akka-actor" % versionOfAkka,
  "com.typesafe.akka" %% "akka-cluster" % versionOfAkka,
  "com.typesafe.akka" %% "akka-kernel" % versionOfAkka,
  "com.typesafe.akka" %% "akka-slf4j" % versionOfAkka,
  "com.typesafe.akka" %% "akka-contrib" % versionOfAkka,
  "com.typesafe.akka" %% "akka-testkit" % versionOfAkka,
  "org.apache.hbase" % "hbase" % "0.94.15-cdh4.7.1",
  "org.apache.hadoop" % "hadoop-common" % "2.0.0-cdh4.7.1",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0"
)
    