package de.ag.radio.listeners

import de.ag.radio._

trait Listener extends java.io.Closeable {
  def isListening(messageType: MessageType, componentPath: List[String], context: Map[String, ContextValue]): Boolean

  /**
    * Note: even if isListening returnd false for some kind of message, this method may still be called with it.
    */
  def process(message: Message): Unit

  final def +(other: Listener): Listener = Sequential(Seq(this, other))

  final def blacklist(componentPrefix: List[String]*): Listener = BlacklistListener(this, componentPrefix :_*)

  final def whitelist(componentPrefix: List[String]*): Listener = WhitelistListener(this, componentPrefix :_*)

  final def restrictTo(mtype: MessageType*): Listener = RestrictListener(this, mtype :_*)
}

object Listener {
  def metrics(l: Listener) = l.restrictTo(
    MessageType.Metric(Audience.Developer), MessageType.Metric(Audience.User))
  def events(l: Listener) = l.restrictTo(
    MessageType.Event(Audience.Developer, Importance.Low), MessageType.Event(Audience.Developer, Importance.High),
    MessageType.Event(Audience.User, Importance.Low), MessageType.Event(Audience.User, Importance.High))

  def user(l: Listener) = l.restrictTo(
    MessageType.Metric(Audience.User),
    MessageType.Event(Audience.User, Importance.Low), MessageType.Event(Audience.User, Importance.High))
  def developer(l: Listener) = l.restrictTo(
    MessageType.Metric(Audience.Developer),
    MessageType.Event(Audience.Developer, Importance.Low), MessageType.Event(Audience.Developer, Importance.High))

  def high(l: Listener) = l.restrictTo(
    MessageType.Event(Audience.Developer, Importance.High), MessageType.Event(Audience.User, Importance.High))
  def low(l: Listener) = l.restrictTo(
    MessageType.Event(Audience.Developer, Importance.Low), MessageType.Event(Audience.User, Importance.Low))

  /*
   // Example:
   val logs = metrics(SLF4Listener.info) + user(high(SLF4Listener.error.withMarker("x")) + low(SLF4Listener.info)) + developer(SLF4Listener.trace)
   */
}
