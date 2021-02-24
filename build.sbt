/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

val scala3Version = "3.0.0-M3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "chips-core",
    version := "0.1.0",

    scalaVersion := scala3Version,

    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-language:implicitConversions",
      "-explain",
    ),

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  )
