package com.ladinu.com.ladinu.workoutlogger.sql

import doobie.util.transactor.Transactor

trait XaProvider {
  import cats.effect._
  val xa: Transactor[IO] = Transactor.fromDriverManager[IO]("org.sqlite.JDBC", "jdbc:sqlite::memory:", "", "")
}
