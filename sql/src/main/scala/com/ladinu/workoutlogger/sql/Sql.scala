package com.ladinu.workoutlogger.sql


import cats.free.Cofree
import cats.implicits._
import com.ladinu.workoutlogger.model.Note
import doobie._
import doobie.implicits._

object Sql {
  object Database {

  val createNotesTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS notes(
            id    INTEGER   NOT NULL PRIMARY KEY,
            text  TEXT      NOT NULL
          );
      """.update

  val createExerciseDescriptionTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS exercise_description(
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
            FOREIGN KEY(exercise_name_id)   REFERENCES exercise_description(id),
            FOREIGN KEY(note_id)            REFERENCES notes(id)
          );
      """.update

  val createExercisesTableIndex: Update0 =
    sql"""
          CREATE INDEX IF NOT EXISTS datetime_index ON exercises(datetime);
       """.update

  val setup: ConnectionIO[Int] =
    createNotesTable.run *>
      createExerciseDescriptionTable.run *>
      createWorkoutsTable.run *>
      createSetsTable.run *>
      createExercisesTable.run *>
      createExercisesTableIndex.run
  }

}

