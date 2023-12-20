ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"


libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "2.1.0",
  "org.jsoup" % "jsoup" % "1.15.4",
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "com.lihaoyi" %% "requests" % "0.8.0",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.3.0",
  "org.reactivemongo" %% "reactivemongo" % "1.0.10",
  "com.typesafe" % "config" % "1.4.3",
  "org.slf4j" % "slf4j-api" % "2.0.5",
  "org.slf4j" % "slf4j-simple" % "2.0.5"
)