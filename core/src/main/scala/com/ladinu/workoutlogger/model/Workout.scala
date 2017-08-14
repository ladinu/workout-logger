package com.ladinu.workoutlogger.model

import java.time.LocalDateTime

import cats.data.NonEmptyList

case class Workout(name: String, notes: Option[Notes], exercises: NonEmptyList[Exercise], timeStamp: LocalDateTime)
