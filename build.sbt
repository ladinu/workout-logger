
val commonSettings = Seq[Setting[_]](
  scalaVersion := "2.12.1",
  version := "1.0",
  organization := "com.ladinu",
  compile := ((compile in Compile) dependsOn (compile in Test)).value
)

lazy val doobieVersion = "0.5.0-M8"
lazy val catsVersion = "1.0.0-MF"



lazy val model = (project in file("model"))
  .settings(commonSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % catsVersion
    )
  )

lazy val sql = (project in file("sql"))
  .settings(commonSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      "org.tpolecat" %% "doobie-core"       % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion,
      "org.xerial" % "sqlite-jdbc"   % "3.7.2"
    )
  )
  .dependsOn(model)
  .aggregate(model)

lazy val root = (project in file("."))
  .settings(commonSettings:_*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
    )
  )
  .dependsOn(`model`, `sql`)
  .aggregate(`model`, `sql`)
