name := "akka-lessons-learnt"

version := "0.0.1"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
	"-deprecation",
	"-encoding", "UTF-8",
	"-feature",
	"-language:existentials",
	"-language:higherKinds",
	"-language:implicitConversions",
	"-unchecked",
	"-Xlint",
	"-Yno-adapted-args",
	"-Ywarn-dead-code",
	"-Ywarn-numeric-widen",
	"-Ywarn-value-discard",
	"-Xfuture",
	"-Ywarn-unused-import",
	"-Xfatal-warnings"
)

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.5.21"
)

fork in run := true
