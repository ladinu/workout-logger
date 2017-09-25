package com.ladinu.workoutlogger.sql


import doobie._, doobie.implicits._
import cats._, cats.data._, cats.effect._, cats.implicits._
import cats.~>

object Sql {
  def createNotesTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS notes(
            id    INTEGER   NOT NULL PRIMARY KEY,
            text  TEXT      NOT NULL
          );
      """.update

  def createExerciseNamesTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS exercise_names(
            id            INTEGER   NOT NULL PRIMARY KEY,
            name          TEXT      NOT NULL,
            description   TEXT
          );
      """.update

  def createWorkoutsTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS workouts(
            id            INTEGER     NOT NULL PRIMARY KEY,
            name          TEXT        NOT NULL,
            note_id       INTEGER,
            FOREIGN KEY(note_id) REFERENCES notes(id)
          );
      """.update

  def createSetsTable: Update0 =
    sql"""
          CREATE TABLE IF NOT EXISTS sets(
            id      INTEGER     NOT NULL PRIMARY KEY,
            weight  DOUBLE      NOT NULL ,
            reps    INTEGER     NOT NULL,
            rest    INTEGER     NOT NULL,
            CONSTRAINT unique_constraint UNIQUE(weight, reps, rest)
          );
      """.update

  def createExercisesTable: Update0 =
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

  def createExercisesTableIndex: Update0 =
    sql"""
          CREATE INDEX IF NOT EXISTS datetime_index ON exercises(datetime);
       """.update

  def setupDB: ConnectionIO[Int] =
    createNotesTable.run *>
      createExerciseNamesTable.run *>
      createWorkoutsTable.run *>
      createSetsTable.run *>
      createExercisesTable.run *>
      createExercisesTableIndex.run
}

object Test extends App {
  val xa = Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC", "jdbc:sqlite::memory:", "", ""
  )


  print(Sql.setupDB.transact(xa).unsafeRunSync)
}
