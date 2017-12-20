package de.ag.radio

@inline
final class EventTransmitter(val that: Transmitter) {
  final def info(audience: Audience, msg: => String) = that.event(audience, Importance.Low, msg)
  final def error(audience: Audience, msg: => String, exception: Option[Throwable]) = that.event(audience, Importance.High, msg, exception)
}

object EventTransmitter {
  @inline
  final def apply(that: Transmitter) = new EventTransmitter(that)
}
