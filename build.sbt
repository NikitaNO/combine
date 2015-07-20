name := "Lift 2.6 starter template"

version := "0.0.4"

organization := "net.liftweb"

scalaVersion := "2.11.1"

resolvers ++= Seq("snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
                  "staging"       at "https://oss.sonatype.org/content/repositories/staging",
                  "releases"      at "https://oss.sonatype.org/content/repositories/releases",
                  "Signpost releases" at "https://oss.sonatype.org/content/repositories/signpost-releases/"
                 )

seq(webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.6-RC1"
  Seq(
    "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
    "net.liftweb"       %% "lift-mapper"        % liftVersion        % "compile",
    "net.liftmodules"   %% "lift-jquery-module_2.6" % "2.8",
    "org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
    "org.eclipse.jetty" % "jetty-plus"          % "8.1.7.v20120910"  % "container,test", // For Jetty Config
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"    % "logback-classic"     % "1.0.6",
    "org.specs2"        %% "specs2"             % "2.3.12"             % "test",
    "com.h2database"    % "h2"                  % "1.3.167",
    "org.twitter4j" % "twitter4j-stream" % "4.0.2",
    "org.json" % "json" % "20090211",
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
    "commons-io" % "commons-io" % "2.4",
    "com.rometools" % "rome" % "1.5.0")
}

libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.0.2"

libraryDependencies += "org.apache.lucene" % "lucene-core" % "5.0.0"

val myProject = project.enablePlugins(DeploySSH)

addCommandAlias("s","; container:start") ++
addCommandAlias("rs","; container:stop; container:start") ++
addCommandAlias("cucs","; container:stop; clean; compile; container:start")

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyJarName in assembly := "combine.jar"

mainClass in assembly := Some("code.Main")

port in container.Configuration := 8088



