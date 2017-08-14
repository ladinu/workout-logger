
val commonSettings = Seq[Setting[_]](
  scalaVersion := "2.12.1",
  version := "1.0",
  organization := "com.ladinu",
  compile := ((compile in Compile) dependsOn (compile in Test)).value
)

lazy val doobieVersion = "0.4.2"


lazy val core = (project in file("core"))
  .settings(commonSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core-cats"       % doobieVersion,
      "org.tpolecat" %% "doobie-postgres-cats"   % doobieVersion,
      "org.tpolecat" %% "doobie-specs2-cats"     % doobieVersion,
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )