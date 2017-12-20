package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

trait Listener extends java.io.Closeable {
  def isListening(componentPath: List[String], messageType: MessageType): Boolean

  /**
    * Note: even if isListening returnd false for some kind of message, this method may still be called with it.
    */
  def process(componentPath: List[String], messageType: MessageType, message: => String, context: Map[String, ContextValue], exception: Option[Throwable], time: Instant): Unit

  final def +(other: Listener): Listener = Sequential(Seq(this, other))

  final def blacklist(componentPrefix: List[String]*): Listener = BlacklistListener(this, componentPrefix :_*)

  final def whitelist(componentPrefix: List[String]*): Listener = WhitelistListener(this, componentPrefix :_*)

  final def restrictTo(mtype: MessageType*): Listener = RestrictListener(this, mtype :_*)
}
