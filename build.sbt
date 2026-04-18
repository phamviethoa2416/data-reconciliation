ThisBuild / organization := "com.evnnpc.reconciliation"
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.12.18"

lazy val sparkVersion = "3.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "data-reconciliation",
    Compile / mainClass := Some("Main"),

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion % Provided,
      "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
      "org.apache.spark" %% "spark-avro" % sparkVersion % Provided,
      "com.oracle.database.jdbc" % "ojdbc8" % "19.3.0.0",
      "com.mysql" % "mysql-connector-j" % "8.0.33",
      "com.microsoft.sqlserver" % "mssql-jdbc" % "12.6.1.jre8"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq("-deprecation", "-feature", "-encoding", "UTF-8"),

    assembly / mainClass := Some("Main"),
    assembly / assemblyJarName := s"${name.value}-${version.value}-assembly.jar",
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case _ => MergeStrategy.first
    }
  )
