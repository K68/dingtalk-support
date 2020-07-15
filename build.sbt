name := "dingtalk-support"

version := "0.0.1"

scalaVersion := "2.13.1"

lazy val `miracle` = project in file(".")

libraryDependencies ++= Seq(
  // play framework & akka
  "com.typesafe.play" %% "play-json" % "2.8.1",

  // monix
  "io.monix" %% "monix" % "3.1.0",

  // sttp
  "com.softwaremill.sttp.client" %% "core" % "2.1.5",
  "com.softwaremill.sttp.client" %% "okhttp-backend" % "2.1.5"

)

// Add dependency on ScalaFX library
//libraryDependencies += "org.scalafx" %% "scalafx" % "14-R19"
//lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
//libraryDependencies ++= javaFXModules.map( m =>
//  "org.openjfx" % s"javafx-$m" % "14.0.1" classifier "win"
//)
