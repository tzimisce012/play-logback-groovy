name := "play-logback-groovy"

organization := "com.github.tzimisce012"

version := "0.2.5"

scalaVersion := "2.11.11"

libraryDependencies += "com.typesafe.play" %% "play-logback" % "2.5.15"

libraryDependencies += "org.codehaus.groovy" % "groovy" % "2.4.12"

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scmInfo := Some(
  ScmInfo(
    url("https://svn.apache.org/viewvc/maven"),
    "git@github.com:tzimisce012/play-logback-groovy.git"
  )
)

developers := List(
  Developer(
    id    = "tzimisce012",
    name  = "Daniel Ochoa Rodriguez",
    email = "dochoa@paradigmadigital.com",
    url   = url("https://www.paradigmadigital.com")
  )
)

publishMavenStyle := true

publishTo := Some("Sonatype Release Nexus" at "https://oss.sonatype.org/content/repositories/releases")

useGpg := true
