package de.ag.radio.listeners

import de.ag.radio._
import java.util.logging.{Logger => JulLogger, Level, LogRecord}

/**
  * A listener that passes messages on via the java.util.logging framework.
  */
case class JulListener(format: MessageTextFormat = JulListener.standardFormat, level: MessageType => Level = JulListener.standardLevels) extends Listener {
  def close() {}

  private def logger(componentPath: Seq[String]): JulLogger = JulLogger.getLogger(componentPath.mkString("."))

  def isListening(componentPath: List[String], messageType: de.ag.radio.MessageType) = logger(componentPath).isLoggable(level(messageType))

  def process(message: Message) = {
    logger(message.componentPath).log {
      val r = new LogRecord(level(message.messageType), format.format(message))
      r.setMillis(message.time.toEpochMilli())
      message.exception.map(r.setThrown _)
      r
    }
  }

  def withFormat(format: MessageTextFormat) = this.copy(format = format)
  def withLevels(level: MessageType => Level) = this.copy(level = level)
}

object JulListener {
  import MessageTextFormat._

  val standardLevels: MessageType => Level = {
    case MessageType.Event(Audience.Developer, Importance.High) => Level.INFO
    case MessageType.Event(Audience.Developer, Importance.Low) => Level.FINE
    case MessageType.Event(Audience.User, Importance.High) => Level.WARNING
    case MessageType.Event(Audience.User, Importance.Low) => Level.INFO
    case MessageType.Metric(Audience.Developer) => Level.FINER
    case MessageType.Metric(Audience.User) => Level.INFO
  }

  val standardFormat = MessageText ++ ContextJson
}
