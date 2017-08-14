package com.ladinu.workoutlogger.model

case class ExerciseSet(weight: Int, reps: Int, restUntilNext: Option[Int], notes: Option[Notes])
