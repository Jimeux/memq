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
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "com.typesafe.play" %% "play-json" % "2.6.0"
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
  "org.typelevel" %% "cats" % "0.9.0",

  // JWT (Secure tokens)
  "com.pauldijou" %% "jwt-core" % "0.12.1",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.57",

  // Simulacrum (Typeclass boilerplate)
  "com.github.mpilquist" %% "simulacrum" % "0.10.0",

  // Shapeless (Generic programming)
  "com.chuusai" %% "shapeless" % "2.3.2"

)

lazy val testDependencies = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-RC1" % Test,
  // Nyaya (Random data generation)
  "com.github.japgolly.nyaya" %% "nyaya-gen" % "0.8.1" % Test
)

lazy val rootDependencies = playDependencies ++ thirdPartyDependencies ++ testDependencies

lazy val metaMacroSettings: Seq[Def.Setting[_]] = Seq(
  scalaVersion := "2.12.1",
  resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.bintrayRepo("scalameta", "maven"),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M8" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  // temporary workaround for https://github.com/scalameta/paradise/issues/10
  scalacOptions in (Compile, console) := Seq() // macroparadise plugin doesn't work in repl yet.
)

lazy val macros = (project in file("macros")).settings(
  metaMacroSettings,
  libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0"
)

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
  .settings(metaMacroSettings)
  .dependsOn(macros)

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
