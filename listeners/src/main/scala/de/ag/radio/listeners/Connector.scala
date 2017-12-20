package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

private[listeners] case class Connector(listener: Listener, componentPath: List[String] = List.empty, context: Map[String, ContextValue] = Map.empty) extends Transmitter {
  override def hasListeners(mtype: MessageType) = listener.isListening(componentPath, mtype)

  override def message(mtype: MessageType, msg: => String, exception: Option[Throwable] = None, time: Option[Instant] = None) = {
    listener.process(componentPath, mtype, msg, context, exception, time.getOrElse(Instant.now()))
  }

  override def within(component: String*) = this.copy(componentPath = this.componentPath ++ component)
  override def withContext(key: String, value: ContextValue) = this.copy(context = this.context + (key -> value))
}
