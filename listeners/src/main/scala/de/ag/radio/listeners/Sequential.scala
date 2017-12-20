package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

class Sequential(val listeners: Seq[Listener]) extends Listener {
  override def close() {
    listeners.foreach(_.close())
  }

  override def isListening(componentPath: List[String], messageType: MessageType) =
    listeners.exists(_.isListening(componentPath, messageType))

  /**
    * Note: even if isEnabled returnd false for some kind of message, this method may still be called with it.
    */
  override def process(componentPath: List[String], messageType: MessageType, message: => String, context: Map[String, ContextValue], exception: Option[Throwable], time: Instant) = {
    listeners.foreach { l =>
      if (l.isListening(componentPath, messageType))
        l.process(componentPath, messageType, message, context, exception, time)
    }
  }
}

object Sequential {
  private def join(listeners: Seq[Listener]): Seq[Listener] = {
    listeners.headOption match {
      case Some(s1) => s1 match {
        case s1: Sequential => s1.listeners ++ join(listeners.tail)
        case _ => s1 +: join(listeners.tail)
      }
      case None => listeners
    }
  }

  def apply(listeners: Seq[Listener]) = new Sequential(join(listeners))
}
