import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

name := """alkobot"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  javaWs,
  "org.webjars" %% "webjars-play" % "2.3.0-1",
  "org.webjars" % "bootstrap" % "3.2.0"
)

pipelineStages := Seq(uglify, digest, gzip)

doc in Compile <<= target.map(_ / "none")

maintainer in Linux := "Alkobot <alkobot@example.com>"

packageSummary in Linux := "My custom package summary"

packageDescription := "My longer package description"

mappings in Universal <+= (packageBin in Compile, baseDirectory ) map { (_, base) =>
     val conf = base / "conf" / "application.conf"
     conf -> "conf/application.conf"
} 
