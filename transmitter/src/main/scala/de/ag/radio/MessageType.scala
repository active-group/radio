package de.ag.radio

sealed trait MessageType
object MessageType {
  /**
    * The message is about something that happened.
    */
  case class Event(audience: Audience, importance: Importance) extends MessageType

  /**
    * The message is about some measured value or duration.
    */
  case class Metric(audience: Audience) extends MessageType

  /*
   /**
   * The message is about a change of state of a component.
   */
   case object StateChange(state: ...) extends MessageType
   */
}
