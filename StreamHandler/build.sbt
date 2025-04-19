name := "Stream Handler"
version := "1.0"
scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.3" % "provided",
  "org.apache.spark" %% "spark-sql" % "3.5.3",
  "com.datastax.spark" %% "spark-cassandra-connector" % "3.5.1",
  "com.datastax.cassandra" % "cassandra-driver-core" % "4.0.0"
)
