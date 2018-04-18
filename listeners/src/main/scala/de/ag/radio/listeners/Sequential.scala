package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

class Sequential(val listeners: Seq[Listener]) extends Listener {
  override def close() {
    listeners.foreach(_.close())
  }

  override def isListening(messageType: MessageType, componentPath: List[String], context: Map[String, ContextValue]) =
    listeners.exists(_.isListening(messageType, componentPath, context))

  override def process(message: Message) = {
    listeners.foreach { l =>
      if (l.isListening(message.messageType, message.componentPath, message.context))
        l.process(message)
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
