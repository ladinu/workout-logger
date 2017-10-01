package com.ladinu.workoutloger.sql

import cats.implicits._
import doobie.implicits._
import doobie.free.connection.ConnectionIO
import org.scalatest.{WordSpec, _}


class NotesTableTest extends WordSpec
  with EmptyDbWithAppliedSchema
  with Matchers {

  "Notes table" must {
    "initially be empty" in {
      val x = 43.pure[ConnectionIO]
      x.transact(xa).unsafeRunSync() should equal(43)
    }
  }
}
