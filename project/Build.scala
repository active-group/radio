import sbt.Keys._
import sbt._

object Build {
  def global_settings =
    Vector(
      organization := "de.active-group",
      version := "0.1.0-SNAPSHOT",
      crossScalaVersions := Seq("2.10.7", "2.11.2", "2.12.4")
    )
}
