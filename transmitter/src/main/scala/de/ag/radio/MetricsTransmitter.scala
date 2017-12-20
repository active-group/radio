package de.ag.radio

import scala.concurrent.duration.Duration
import scala.concurrent.Future

@inline
final class MetricTransmitter(val that: Transmitter) {
  import scala.concurrent.duration._

  final def timeMetric(audience: Audience, msg: Duration => String)(value: Duration): Unit = {
    that.withContext("metric.valueNanos", value.toNanos).metric(audience, msg(value))
  }

  final def syncTimeMetric[A](audience: Audience, msg: Duration => String)(body: => A): A = {
    that.ifListeners(MessageType.Metric(audience), body) {
      val start = System.nanoTime()
      // try-finally here? probably not? optional?
      val res: A = body
      val end = System.nanoTime()
      timeMetric(audience, msg)(Duration.fromNanos(end - start))
      res
    }
  }

  final def asyncTimeMetric[A](audience: Audience, msg: Duration => String)(f: => Future[A])(implicit context: scala.concurrent.ExecutionContext): Future[A] = {
    that.ifListeners(MessageType.Metric(audience), f) {
      val start = System.nanoTime()
      f.map { res =>
        val end = System.nanoTime()
        timeMetric(audience, msg)(Duration.fromNanos(end - start))
        res
      }
    }
  }
}

object MetricTransmitter {
  @inline
  final def apply(that: Transmitter) = new MetricTransmitter(that)
}
