package com.ladinu.workoutlogger.model

import java.time.LocalDateTime

import cats.data.NonEmptyList

case class Workout(name: String, note: Option[Note], exercises: NonEmptyList[Exercise], timeStamp: LocalDateTime)
