package com.ladinu.workoutloger.sql

import doobie.util.transactor.Transactor

trait XaProvider {
  import cats.effect._
  val xa: Transactor[IO] = Transactor.fromDriverManager[IO]("org.sqlite.JDBC", "jdbc:sqlite::memory:", "", "")
}
