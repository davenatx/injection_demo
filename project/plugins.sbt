libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % "0.12.0-0.2.11.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.0")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.2.0")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"