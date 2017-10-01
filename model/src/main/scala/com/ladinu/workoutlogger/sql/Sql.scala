package com.ladinu.workoutlogger.sql


import doobie._
import doobie.implicits._
import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import cats.~>
import com.ladinu.workoutlogger.model.Note
object Sql {
  object Database {

  val createNotesTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS notes(
            id    INTEGER   NOT NULL PRIMARY KEY,
            text  TEXT      NOT NULL
          );
      """.update

  val createExerciseNamesTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS exercise_names(
            id            INTEGER   NOT NULL PRIMARY KEY,
            name          TEXT      NOT NULL,
            description   TEXT
          );
      """.update

  val createWorkoutsTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS workouts(
            id            INTEGER     NOT NULL PRIMARY KEY,
            name          TEXT        NOT NULL,
            note_id       INTEGER,
            FOREIGN KEY(note_id) REFERENCES notes(id)
          );
      """.update

  val createSetsTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS sets(
            id      INTEGER     NOT NULL PRIMARY KEY,
            weight  DOUBLE      NOT NULL ,
            reps    INTEGER     NOT NULL,
            rest    INTEGER     NOT NULL,
            CONSTRAINT unique_constraint UNIQUE(weight, reps, rest)
          );
      """.update

  val createExercisesTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS exercises(
            datetime         INTEGER    NOT NULL,
            set_id           INTEGER    NOT NULL,
            workout_id       INTEGER    NOT NULL,
            exercise_name_id INTEGER    NOT NULL,
            note_id          INTEGER,
            FOREIGN KEY(set_id)             REFERENCES sets(id),
            FOREIGN KEY(workout_id)         REFERENCES workouts(id),
            FOREIGN KEY(exercise_name_id)   REFERENCES exercise_names(id),
            FOREIGN KEY(note_id)            REFERENCES notes(id)
          );
      """.update

  val createExercisesTableIndex: Update0 =
    sql"""
          CREATE INDEX IF NOT EXISTS datetime_index ON exercises(datetime);
       """.update

  val setup: ConnectionIO[Int] =
    createNotesTable.run *>
      createExerciseNamesTable.run *>
      createWorkoutsTable.run *>
      createSetsTable.run *>
      createExercisesTable.run *>
      createExercisesTableIndex.run
  }

  object Query {
    def insertNote(note: Note): Update0 =
      sql"""
            INSERT INTO notes(text) VALUES(${note.content});
         """.update
  }
}

object Test extends App {
  val xa = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC", "jdbc:sqlite::memory:s", "", ""
  )

  println(
    Sql.Query.insertNote(Note("x")).ge
  )

  println(Sql.Database.setup.transact(xa).unsafeRunSync)
  println(Sql.Query.insertNote(Note("test")).run.transact(xa).unsafeRunSync)
}
