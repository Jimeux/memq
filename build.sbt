/*+**********+
  + Settings +
  +***********/
lazy val commonSettings = Seq(
  organization := "io.memq",
  scalacOptions := Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-language:higherKinds",
    "-Xfatal-warnings"
  )
)

/*+**************+
  + Dependencies +
  +***************/
lazy val playDependencies = Seq(
  filters,
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1",
  "com.typesafe.play" %% "play-json" % "2.6.6"
)

lazy val thirdPartyDependencies = Seq(

  // Macwire (Dependency injection)
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided,
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % Provided,
  "com.softwaremill.macwire" %% "util" % "2.3.0",
  "com.softwaremill.macwire" %% "proxy" % "2.3.0",

  // Postgresql (Database driver)
  "org.postgresql" % "postgresql" % "9.4.1212",

  // Cats (Functional programming)
  "org.typelevel" %% "cats-core" % "1.0.0-RC1",

  // JWT (Secure tokens)
  "com.pauldijou" %% "jwt-core" % "0.14.1",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.57",

  // Simulacrum (Typeclass boilerplate)
  "com.github.mpilquist" %% "simulacrum" % "0.10.0",

  // Shapeless (Generic programming)
  "com.chuusai" %% "shapeless" % "2.3.2"

)

lazy val testDependencies = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  // Nyaya (Random data generation)
  "com.github.japgolly.nyaya" %% "nyaya-gen" % "0.8.1" % Test
)

lazy val rootDependencies = playDependencies ++ thirdPartyDependencies ++ testDependencies

/*+**************+
  + Root project +
  +***************/
lazy val root = (project in file("."))
  .enablePlugins(Play)
  .settings(
    commonSettings,
    version := "1.0",
    scalaVersion := "2.12.3",
    libraryDependencies ++= rootDependencies,
    dependencyOverrides ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.5.4",
      "com.typesafe.akka" %% "akka-actor" % "2.5.4"
    ),
    routesGenerator := InjectedRoutesGenerator,
    scalacOptions += "-Ypartial-unification"
  )
