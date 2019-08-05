
name := "template"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "ch.megard" %% "akka-http-cors" % "0.4.0",
  "net.debasishg" %% "redisclient" % "3.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.17",
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-stream" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.17",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.17",
  "org.flywaydb" % "flyway-core" % "4.2.0",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.github.tminglei" %% "slick-pg" % "0.16.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.1",
  "tyrex" % "tyrex" % "1.0.1",
  "org.typelevel" %% "cats-core" % "1.4.0",
  "org.typelevel" %% "cats-free" % "1.4.0",
  "com.github.kxbmap" %% "configs" % "0.4.4",
  "io.spray" %% "spray-json" % "1.3.5",
  "com.outworkers"  %% "phantom-jdk8" % "2.24.2",
  "com.outworkers"  %% "phantom-dsl" % "2.24.2",
  "com.outworkers"  %% "phantom-connectors" % "2.24.2",
  "ai.bale.server" %% "commons-kafka" % "1.0.100",
  "im.actor" %% "akka-scalapb-serialization" % "0.1.19",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "ai.bale" %% "rabbit9-core" % "1.2.12",
  "biz.paluch.logging" % "logstash-gelf" % "1.8.0",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5"

)


PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

PB.targets in Compile := Seq(
  scalapb.gen(grpc=false) -> (sourceManaged in Compile).value
)

fork in run := false
