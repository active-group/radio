package de.ag.radio.listeners

import de.ag.radio._

class RestrictListener(next: Listener, restrictTo: Set[MessageType]) extends FilterListener(next) {
  protected override def filter(messageType: MessageType, componentPath: List[String], context: Map[String, ContextValue]) = restrictTo.contains(messageType)
}

object RestrictListener {
  final def apply(next: Listener, restrictTo: MessageType*) = new RestrictListener(next, restrictTo.toSet)
}
