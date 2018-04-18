package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

private[listeners] abstract class BlackWhiteListListener(next: Listener, componentPathPrefixes: Set[List[String]]) extends FilterListener(next) {
  protected override def filter(messageType: MessageType, componentPath: List[String], context: Map[String, ContextValue]) =
    cond(componentPathPrefixes.exists(componentPath.startsWith(_)))

  protected def cond(v: Boolean): Boolean
}

class BlacklistListener(next: Listener, componentPathPrefixes: Set[List[String]]) extends BlackWhiteListListener(next, componentPathPrefixes) {
  override protected final def cond(v: Boolean) = ! v
}

object BlacklistListener {
  final def apply(next: Listener, componentPathPrefix: List[String]*) = new BlacklistListener(next, componentPathPrefix.toSet)
}

class WhitelistListener(next: Listener, componentPathPrefixes: Set[List[String]]) extends BlackWhiteListListener(next, componentPathPrefixes) {
  override protected final def cond(v: Boolean) = v
}

object WhitelistListener {
  final def apply(next: Listener, componentPathPrefix: List[String]*) = new WhitelistListener(next, componentPathPrefix.toSet)
}
