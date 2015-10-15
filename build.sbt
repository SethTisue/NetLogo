lazy val root =
   project.in(file(".")).
   aggregate(netlogo, parserJVM)

lazy val netlogo = project.in(file("netlogo-gui"))
   .dependsOn(parserJVM)
   .settings(Defaults.coreDefaultSettings ++
             Testing.settings ++
             Packaging.settings ++
             Running.settings ++
             Dump.settings ++
             Scaladoc.settings ++
             ChecksumsAndPreviews.settings ++
             Extensions.extensionsTask ++
             InfoTab.infoTabTask ++
             ModelIndex.modelIndexTask ++
             NativeLibs.nativeLibsTask ++
             Depend.dependTask: _*)
  .settings(
    scalaVersion := "2.11.7",
    organization := "org.nlogo",
    name := "NetLogo",
    onLoadMessage := "",
    resourceDirectory in Compile := baseDirectory.value / "resources",
    scalacOptions ++=
      "-deprecation -unchecked -feature -Xfatal-warnings -Xcheckinit -encoding us-ascii"
      .split(" ").toSeq,
    javacOptions ++=
      "-g -deprecation -encoding us-ascii -Werror -Xlint:all -Xlint:-serial -Xlint:-fallthrough -Xlint:-path -source 1.8 -target 1.8"
      .format(java.io.File.pathSeparator)
      .split(" ").toSeq,
    // only log problems plz
    ivyLoggingLevel := UpdateLogging.Quiet,
    // this makes jar-building and script-writing easier
    retrieveManaged := true,
    // we're not cross-building for different Scala versions
    crossPaths := false,
    scalaSource in Compile := baseDirectory.value / "src" / "main",
    scalaSource in Test    := baseDirectory.value / "src" / "test",
    javaSource in Compile  := baseDirectory.value / "src" / "main",
    javaSource in Test     := baseDirectory.value / "src" / "test",
    unmanagedSourceDirectories in Test      += baseDirectory.value / "src" / "tools",
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    mainClass in (Compile, run)        := Some("org.nlogo.app.App"),
    mainClass in (Compile, packageBin) := Some("org.nlogo.app.App"),
    sourceGenerators in Compile += EventsGenerator.task.taskValue,
    sourceGenerators in Compile += JFlexRunner.task.taskValue,
    resourceGenerators in Compile <+= I18n.resourceGeneratorTask,
    threed := { System.setProperty("org.nlogo.is3d", "true") },
    nogen  := { System.setProperty("org.nlogo.noGenerator", "true") },
    Extensions.extensionRoot := file("extensions"),
    libraryDependencies ++= Seq(
      "org.ow2.asm" % "asm-all" % "5.0.3",
      "org.picocontainer" % "picocontainer" % "2.13.6",
      "log4j" % "log4j" % "1.2.16",
      "javax.media" % "jmf" % "2.1.1e",
      "org.pegdown" % "pegdown" % "1.5.0",
      "org.parboiled" % "parboiled-java" % "1.0.2",
      "steveroy" % "mrjadapter" % "1.2" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/mrjadapter-1.2.jar",
      "org.jhotdraw" % "jhotdraw" % "6.0b1" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/jhotdraw-6.0b1.jar",
      "ch.randelshofer" % "quaqua" % "7.3.4" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/quaqua-7.3.4.jar",
      "ch.randelshofer" % "swing-layout" % "7.3.4" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/swing-layout-7.3.4.jar",
      "org.jogl" % "jogl" % "1.1.1" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/jogl-1.1.1.jar",
      "org.gluegen-rt" % "gluegen-rt" % "1.1.1" from "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/gluegen-rt-1.1.1.jar",
      "org.jmock" % "jmock" % "2.5.1" % "test",
      "org.jmock" % "jmock-legacy" % "2.5.1" % "test",
      "org.jmock" % "jmock-junit4" % "2.5.1" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.apache.httpcomponents" % "httpclient" % "4.2",
      "org.apache.httpcomponents" % "httpmime" % "4.2",
      "com.googlecode.json-simple" % "json-simple" % "1.1.1"
    ),
    all <<= (baseDirectory, streams) map { (base, s) =>
      s.log.info("making resources/system/dict.txt and docs/dict folder")
      IO.delete(base / "docs" / "dict")
      Process("python bin/dictsplit.py").!!
    },
    all <<= all.dependsOn(
      packageBin in Test,
      Extensions.extensions,
      NativeLibs.nativeLibs,
      ModelIndex.modelIndex,
      InfoTab.infoTab,
      Scaladoc.docSmaller))

lazy val commonSettings = Seq(
  organization := "org.nlogo",
  licenses += ("GPL-2.0", url("http://opensource.org/licenses/GPL-2.0")),
  scalacOptions ++=
    "-deprecation -unchecked -feature -Xcheckinit -encoding us-ascii -target:jvm-1.7 -Xlint -Xfatal-warnings"
      .split(" ").toSeq,
  scalaSource in Compile := baseDirectory.value / "src" / "main",
  scalaSource in Test := baseDirectory.value / "src" / "test",
  ivyLoggingLevel := UpdateLogging.Quiet,
  logBuffered in testOnly in Test := false,
  onLoadMessage := "",
  scalaVersion := "2.11.7",
  // don't cross-build for different Scala versions
  crossPaths := false,
  scalastyleTarget in Compile := {
    file("target") / s"scalastyle-result-${name.value}.xml"
  }
)

lazy val jvmSettings = Seq(
  javacOptions ++=
    "-g -deprecation -encoding us-ascii -Werror -Xlint:all -Xlint:-serial -Xlint:-fallthrough -Xlint:-path -source 1.8 -target 1.8"
    .split(" ").toSeq,
  javaSource in Compile := baseDirectory.value / "src" / "main",
  javaSource in Test := baseDirectory.value / "src" / "test",
  publishArtifact in Test := true
)

lazy val scalatestSettings = Seq(
  // show test failures again at end, after all tests complete.
  // T gives truncated stack traces; change to G if you need full.
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oT"),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    // Using a 1.12.2 until fix is available for https://github.com/rickynils/scalacheck/issues/173
    "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
  )
)

lazy val testSettings = scalatestSettings ++ Seq(
  libraryDependencies ++= Seq(
    "org.jmock" % "jmock" % "2.8.1" % "test",
    "org.jmock" % "jmock-legacy" % "2.8.1" % "test",
    "org.jmock" % "jmock-junit4" % "2.8.1" % "test",
    "org.reflections" % "reflections" % "0.9.10" % "test",
    "org.slf4j" % "slf4j-nop" % "1.7.12" % "test"
  )
)

lazy val publicationSettings =
  bintrayPublishSettings ++
  Seq(
    bintray.Keys.repository in bintray.Keys.bintray := "NetLogoHeadless",
    bintray.Keys.bintrayOrganization in bintray.Keys.bintray := Some("netlogo")
  )

lazy val docOptions = Seq(
  netlogoVersion := {
    (testLoader in Test).value
      .loadClass("org.nlogo.api.Version")
      .getMethod("version")
      .invoke(null).asInstanceOf[String]
      .stripPrefix("NetLogo ")
  },
  scalacOptions in (Compile, doc) ++= {
    Seq("-encoding", "us-ascii") ++
    Opts.doc.title("NetLogo") ++
    Opts.doc.version(version.value) ++
    Opts.doc.sourceUrl("https://github.com/NetLogo/NetLogo/blob/" +
      version.value + "/src/main€{FILE_PATH}.scala")
  },
  sources in (Compile, doc) ++= (sources in (parserJVM, Compile)).value,
  // compensate for issues.scala-lang.org/browse/SI-5388
  doc in Compile := {
    val path = (doc in Compile).value
    for (file <- Process(Seq("find", path.toString, "-name", "*.html")).lines)
      IO.write(
        new File(file),
        IO.read(new File(file)).replaceAll("\\.java\\.scala", ".java"))
    path
  }
)

lazy val parserSettings: Seq[Setting[_]] = Seq(
  isSnapshot := true,
  name := "parser",
  version := "0.0.1",
  unmanagedSourceDirectories in Compile += file(".").getAbsoluteFile / "parser-core" / "src" / "main"
)

lazy val sharedResources = (project in file ("shared")).
  settings(commonSettings: _*)

// this project exists only to allow parser-core to be scalastyled
lazy val parserCore = (project in file("parser-core")).
  settings(commonSettings: _*).
  settings(skip in (Compile, compile) := true)

lazy val parserJVM = (project in file("parser-jvm")).
  dependsOn(sharedResources).
  settings(commonSettings: _*).
  settings(parserSettings: _*).
  settings(jvmSettings: _*).
  settings(scalatestSettings: _*).
  settings(
      mappings in (Compile, packageBin) ++= mappings.in(sharedResources, Compile, packageBin).value,
      mappings in (Compile, packageSrc) ++= mappings.in(sharedResources, Compile, packageSrc).value,
      libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3"
    )

lazy val netlogoVersion = taskKey[String]("from api.Version")
lazy val all = TaskKey[Unit]("all", "build everything!!!")
lazy val threed = TaskKey[Unit]("threed", "enable NetLogo 3D")
lazy val nogen = TaskKey[Unit]("nogen", "disable bytecode generator")

