package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

private [listeners] abstract class FilterListener(next: Listener) extends Listener {
  protected def filter(messageType: MessageType, componentPath: List[String], context: Map[String, ContextValue]): Boolean

  override def close() { next.close() }

  override def isListening(messageType: MessageType, componentPath: List[String], context: Map[String, ContextValue]): Boolean = {
    filter(messageType, componentPath, context) && next.isListening(messageType, componentPath, context)
  }

  override def process(message: Message) {
    if (filter(message.messageType, message.componentPath, message.context)) next.process(message)
  }
}
