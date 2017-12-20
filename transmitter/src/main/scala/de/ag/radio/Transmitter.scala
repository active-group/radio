package de.ag.radio

import java.time.Instant

trait Transmitter {
  // https://github.com/typesafehub/scala-logging#line-numbers-in-log-message ?

  def hasListeners(mtype: MessageType): Boolean
  def message(mtype: MessageType, msg: => String, exception: Option[Throwable] = None, time: Option[Instant] = None): Unit

  def within(component: String*): Transmitter
  def withContext(key: String, value: ContextValue): Transmitter

  final def ifListeners[A](mtype: MessageType, otherwise: => A)(then: => A): A = {
    if (hasListeners(mtype)) then else otherwise
  }

  final def wrapIfListeners[A](mtype: MessageType, wrap: (=> A) => A)(f: => A): A = {
    ifListeners(mtype, f) {
      wrap(f)
    }
  }

  // TODO: forcefuly change audience, change severity...?

  final def event(audience: Audience, importance: Importance, msg: => String, exception: Option[Throwable] = None) = message(MessageType.Event(audience, importance), msg, exception)
  final def metric(audience: Audience, msg: => String) = message(MessageType.Metric(audience), msg)

  // def stateChange(...) ?

  final def withContext(key: String, value: String): Transmitter = withContext(key, ContextString(value))
  final def withContext(key: String, value: Long): Transmitter = withContext(key, ContextInteger(value))
  final def withContext(key: String, value: Boolean): Transmitter = withContext(key, ContextBoolean(value))
}

