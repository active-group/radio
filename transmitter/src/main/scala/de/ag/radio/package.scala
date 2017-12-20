package de.ag

package object radio {
  implicit def metricTransmitter(that: Transmitter) = MetricTransmitter(that)
  implicit def eventTransmitter(that: Transmitter) = EventTransmitter(that)
}
