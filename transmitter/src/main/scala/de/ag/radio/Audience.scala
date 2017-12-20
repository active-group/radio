package de.ag.radio

sealed trait Audience
object Audience {
  /**
    * Message is directed to developers of the system.
    * with Serverity.Low: traces; detailed metrics. details for post-mortem analysis
    * with Serverity.High: bugs, unexpected conditions, e.g. as last message before exit.
    */
  case object Developer extends Audience

  /**
    * Message is directed to users of the system.
    * with Severity.Low: who did what; performance metrics.
    * with Severity.High: no connection to external systems; corrupt config files; out of memory.
    */
  case object User extends Audience
}
