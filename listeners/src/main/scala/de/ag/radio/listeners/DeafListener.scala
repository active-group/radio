package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

object DeafListener extends Listener {
  override def close() { }
  override def isListening(mtype: MessageType, componentPath: List[String], context: Map[String, ContextValue]) = false
  override def process(message: Message) { }
}
