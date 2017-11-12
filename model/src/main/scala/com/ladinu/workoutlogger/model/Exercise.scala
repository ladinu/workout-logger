package com.ladinu.workoutlogger.model

import java.time.LocalDateTime

import cats.data.NonEmptyList

case class Exercise(exerciseDescription: ExerciseDescription, sets: NonEmptyList[ExerciseSet], notes: Option[Note], timeStamp: LocalDateTime)
