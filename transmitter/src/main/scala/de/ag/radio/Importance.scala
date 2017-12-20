package de.ag.radio

sealed trait Importance
object Importance {
  /**
    * Informational message; the system is fine and could do everything it's supposed to do.
    */
  case object Low extends Importance

  /**
    * Something went wrong; the system could not do what it's supposed to do.
    */
  case object High extends Importance
}

