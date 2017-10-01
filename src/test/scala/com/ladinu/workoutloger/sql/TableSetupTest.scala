package com.ladinu.workoutloger.sql

import com.ladinu.workoutloger.sql.tags.DbSetup
import com.ladinu.workoutlogger.sql.Sql
import doobie.implicits._
import org.scalatest.{WordSpec, _}

class TableSetupTest extends WordSpec
  with XaProvider
  with Matchers {

  "Database setup"  must {
    "throw no execptions" taggedAs DbSetup in {

      noException mustBe thrownBy {
        Sql.Database.setup.transact(xa).unsafeRunSync() should equal(0)
      }
    }
  }
}
