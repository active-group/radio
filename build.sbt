// Build definitions

lazy val transmitter = project.in(file("transmitter")).
  settings(Build.global_settings :_*).
  settings(
    name := "radio"
  )
  
lazy val listeners = project.in(file("listeners")).
  settings(Build.global_settings :_*).
  settings(
    name := "radio-listeners",
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
  ).
  dependsOn(transmitter)

lazy val root = Project(
  id = "root",
  base = file("."),
  aggregate = Seq(transmitter, listeners),
  settings = Build.global_settings ++ Seq(
    packagedArtifacts := Map.empty           // prevent publishing anything!
  )
)
