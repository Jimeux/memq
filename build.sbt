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
    "-language:higherKinds"
  )
)

/*+**************+
  + Dependencies +
  +***************/
lazy val playDependencies = Seq(
  filters,
  "com.typesafe.play" % "play-slick_2.12" % "3.0.0-RC1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-RC1",
  "com.typesafe.play" %% "play-json" % "2.6.0-M7"
)

lazy val thirdPartyDependencies = Seq(

  // Macwire (Dependency Injection)
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided,
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % Provided,
  "com.softwaremill.macwire" %% "util" % "2.3.0",
  "com.softwaremill.macwire" %% "proxy" % "2.3.0",

  // Database Driver
  "org.postgresql" % "postgresql" % "9.4.1212",

  // Cats (Functional Programming)
  "org.typelevel" %% "cats" % "0.9.0",

  // JWT (Secure Tokens)
  "com.pauldijou" %% "jwt-core" % "0.12.1",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.57"

)

lazy val testDependencies = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-RC1" % Test
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
    scalaVersion := "2.12.1",
    libraryDependencies ++= rootDependencies,
    routesGenerator := InjectedRoutesGenerator,
    scalacOptions += "-Ypartial-unification"
  )

/*+*********************+
  + Performance project +
  +**********************/
lazy val performance = (project in file("performance"))
  .enablePlugins(GatlingPlugin)
  .settings(
    commonSettings,
    scalaVersion := "2.11.8",
    target in Gatling := new File("./reports"),
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.5" % "test,it",
      "io.gatling" % "gatling-test-framework" % "2.2.5" % "test,it"
    )
  )
