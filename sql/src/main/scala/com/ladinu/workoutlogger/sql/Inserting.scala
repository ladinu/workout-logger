package com.ladinu.workoutlogger.sql

import java.time.LocalDateTime

import cats.implicits._
import com.ladinu.workoutlogger.model.{Exercise, ExerciseDescription, Note}
import doobie._
import doobie.implicits._
object Inserting {

  sealed trait IdAnd[T] {
    def id: Int
    def data: T
  }

  object IdAnd {
    protected[Inserting] def apply[T](dbId: Int, dbData: T): IdAnd[T] = new IdAnd[T] {
      override def id: Int = dbId
      override def data: T = dbData
    }

    def unapply[T](arg: IdAnd[T]): Option[(Int, T)] = Some((arg.id, arg.data))
  }

  sealed trait Workout {
    def name: String
    def note: Option[IdAnd[Note]]
  }

  object Workout {
    protected[Inserting] def apply(dbName: String, dbNote: Option[IdAnd[Note]]): Workout = new Workout {
      override def name: String = dbName
      override def note: Option[IdAnd[Note]] = dbNote
    }
    def unapply(arg: Workout): Option[(String, Option[IdAnd[Note]])] = Some((arg.name, arg.note))
  }

  sealed trait Set {
    def weight: Double
    def reps: Int
    def rest: Int
  }

  object Set {
    protected[Inserting] def apply(dbWeight: Double, dbReps: Int, dbRest: Int): Set = new Set {
      override def weight: Double  = dbWeight
      override def reps: Int = dbReps
      override def rest: Int = dbRest
    }

    def unapply(arg: Set): Option[(Double, Int, Int)] = Some(arg.weight, arg.reps, arg.rest)
  }

  def insertWorkout(name: String, inputNote: Option[IdAnd[Note]]): ConnectionIO[IdAnd[Workout]] = for {
    id <- inputNote match {
      case Some(noteId) =>
        sql"""INSERT INTO workouts(name, note_id) VALUES($name, ${noteId.id});""".update.withUniqueGeneratedKeys[Int]("id")
      case None =>
        sql"""INSERT INTO workouts(name) VALUES($name);""".update.withUniqueGeneratedKeys[Int]("id")
    }
    pair <- sql"""SELECT id, name FROM workouts WHERE id=$id""".query[Tuple2[Int, String]].unique

    } yield IdAnd[Workout](pair._1, Workout(pair._2, inputNote))

  def insertExerciseDescription(name: String, description: Option[String]): ConnectionIO[IdAnd[ExerciseDescription]] = for {
    id <- sql"""INSERT INTO exercise_description(name, description) VALUES($name, $description);""".update.withUniqueGeneratedKeys[Int]("id")
    p <- sql"SELECT id, name, description FROM exercise_description WHERE id=$id".query[Tuple2[Int, ExerciseDescription]].unique
  } yield IdAnd(p._1, p._2)


  def insertNote(note: String): ConnectionIO[IdAnd[Note]]  = for {
    id <- sql"""INSERT INTO notes(text) VALUES($note);""".update.withUniqueGeneratedKeys[Int]("id")
    p <- sql"SELECT id, text FROM notes WHERE id=$id".query[Tuple2[Int, Note]].unique
  } yield IdAnd(p._1, p._2)

  def insertSet(weight: Double, reps: Int, rest: Int): ConnectionIO[IdAnd[Set]] = for {
    id <- sql"""INSERT INTO sets(weight, reps, rest) VALUES($weight, $reps, $rest);""".update.withUniqueGeneratedKeys[Int]("id")
    p <- sql"SELECT id, weight, reps, rest FROM sets WHERE id=$id".query[Tuple4[Int, Double, Int, Int]].unique
  } yield IdAnd(p._1, Set(p._2, p._3, p._4))

  def insertExercise(datetime: Option[Int],
                     set: IdAnd[Set],
                     workout: IdAnd[Workout],
                     exerciseDescription: IdAnd[ExerciseDescription],
                     note: Option[IdAnd[Note]]): Update0 = {

    note match {
      case None => sql"""INSERT INTO exercises VALUES (1, ${set.id}, ${workout.id}, ${exerciseDescription.id}, null)""".update
      case Some(IdAnd(id, _)) => sql"""INSERT INTO exercises VALUES (1, ${set.id}, ${workout.id}, ${exerciseDescription.id}, $id)""".update
    }
  }

}
