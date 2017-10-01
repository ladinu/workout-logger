package com.ladinu.workoutloger.sql

import com.ladinu.workoutlogger.sql.Sql
import doobie.implicits._

trait EmptyDbWithAppliedSchema extends XaProvider {
  Sql.Database.setup.transact(xa).unsafeRunSync()
}
