package com.ladinu.workoutlogger.model

import java.time.LocalDateTime

import cats.data.NonEmptyList

case class Exercise(name: String, description: String, sets: NonEmptyList[ExerciseSet], notes: Option[Note], timeStamp: LocalDateTime)
