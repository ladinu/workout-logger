package com.ladinu.com.ladinu.workoutlogger.sql

import cats.implicits.catsSyntaxCartesian
import com.ladinu.workoutlogger.generators.model.NoteGenerator
import com.ladinu.workoutlogger.model.{ExerciseDescription, Note}
import com.ladinu.workoutlogger.sql.Inserting.{IdAnd, Set, Workout}
import com.ladinu.workoutlogger.sql.{Inserting, Sql}
import doobie._
import doobie.implicits._
import org.scalactic._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Inside, Matchers, ParallelTestExecution, WordSpec}


class TableTests extends WordSpec
  with XaProvider
  with NoteGenerator
  with PropertyChecks
  with Matchers
  with Inside
  with ParallelTestExecution {

  "Notes table" must {
    "initially be empty" in {
      val cmd: ConnectionIO[Int] = sql"SELECT COUNT(*) FROM notes;".query[Int].unique
      (Sql.Database.setup *> cmd).transact(xa).unsafeRunSync() should equal(0)
    }

    "allow notes to be inserted" in {
      forAll { text: String =>
        inside((Sql.Database.setup *> Inserting.insertNote(text)).transact(xa).unsafeRunSync()) {
          case IdAnd(_, note) => note should equal(Note(text))
        }
      }
    }
  }

  "Exercise Description table" must {

    "allow exercise name and description to be written and red" in {
      forAll { (name: String, description: Option[String]) =>
        inside((Sql.Database.setup *> Inserting.insertExerciseDescription(name, description)).transact(xa).unsafeRunSync()) {
          case IdAnd(_, exerciseDescription) => exerciseDescription should equal(ExerciseDescription(name, description))
        }
      }
    }
  }

  "Workouts table" must {
    "allow exercises to be inserted" in {
      forAll { name: String =>
        inside((Sql.Database.setup *> Inserting.insertWorkout(name, None)).transact(xa).unsafeRunSync()) {
          case IdAnd(_, Workout(workoutName, None)) => workoutName should equal(name)
          case _ => fail("Data did not match")
        }
      }
    }
  }

  "Sets table" must {
    "allow sets to be inserted" in {

      forAll { (weight: Double, reps: Int, rest: Int) =>
        inside((Sql.Database.setup *> Inserting.insertSet(weight, reps, rest)).transact(xa).unsafeRunSync()) {
          case IdAnd(_, Set(dbWeight, dbReps, dbRest)) =>
            (dbReps, dbRest) should equal(reps, rest)
            // TODO: Tollerance check
            // dbWeight should === (weight)
        }
      }
    }
  }

  "Exercises table" must {
    "allow exercises to be inserted" in {
      noException shouldBe thrownBy {
        (for {
          _ <- Sql.Database.setup
          set <- Inserting.insertSet(45.0, 1, 1)
          workout <- Inserting.insertWorkout("test", None)
          exerciseDescription <- Inserting.insertExerciseDescription("test", None)
          a <- Inserting.insertExercise(None, set, workout, exerciseDescription, None).run
        } yield a).transact(xa).unsafeRunSync()
      }
    }
  }
}
