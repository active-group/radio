package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

private [listeners] abstract class FilterListener(next: Listener) extends Listener {
  protected def filter(componentPath: List[String], messageType: MessageType): Boolean

  override def close() { next.close() }

  override def isListening(componentPath: List[String], messageType: MessageType): Boolean = {
    filter(componentPath, messageType) && next.isListening(componentPath, messageType)
  }

  override def process(componentPath: List[String], messageType: MessageType, message: => String, context: Map[String, ContextValue], exception: Option[Throwable], time: Instant) {
    if (filter(componentPath, messageType)) next.process(componentPath, messageType, message, context, exception, time)
  }
}
