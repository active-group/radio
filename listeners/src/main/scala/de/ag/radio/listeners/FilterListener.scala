package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

private [listeners] abstract class FilterListener(next: Listener) extends Listener {
  protected def filter(componentPath: List[String], messageType: MessageType): Boolean

  override def close() { next.close() }

  override def isListening(componentPath: List[String], messageType: MessageType): Boolean = {
    filter(componentPath, messageType) && next.isListening(componentPath, messageType)
  }

  override def process(message: Message) {
    if (filter(message.componentPath, message.messageType)) next.process(message)
  }
}
