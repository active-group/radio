package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

case class Message(
  messageType: MessageType,
  componentPath: List[String],
  text: String,
  context: Map[String, ContextValue],
  exception: Option[Throwable],
  time: Instant)
