package com.ladinu.workoutlogger.generators.model

import com.ladinu.workoutlogger.model.Note
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

trait NoteGenerator {
  implicit def arbitraryNote: Arbitrary[Note] = Arbitrary(arbitrary[String].map(Note))
}
