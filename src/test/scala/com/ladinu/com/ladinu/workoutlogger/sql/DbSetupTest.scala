package com.ladinu.com.ladinu.workoutlogger.sql

import com.ladinu.com.ladinu.workoutlogger.sql.tags.DbSetup
import com.ladinu.workoutlogger.sql.Sql
import doobie.implicits._
import org.scalatest.{WordSpec, _}
import cats.implicits._

class DbSetupTest extends WordSpec
  with XaProvider
  with Matchers {

  "Database setup"  must {
    "throw no execptions" taggedAs DbSetup in {
      noException mustBe thrownBy {
        Sql.Database.setup.transact(xa).unsafeRunSync() should equal(0)
      }
    }

    "not throw exceptions if run twice" taggedAs DbSetup in {
      noException mustBe thrownBy {
        (Sql.Database.setup *> Sql.Database.setup).transact(xa).unsafeRunSync() should equal(0)
      }
    }
  }
}
