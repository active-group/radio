package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

object DeafListener extends Listener {
  override def close() { }
  override def isListening(componentPath: List[String], mtype: MessageType) = false
  override def process(componentPath: List[String], messageType: MessageType, message: => String, context: Map[String, ContextValue], exception: Option[Throwable], time: Instant) { }
}
