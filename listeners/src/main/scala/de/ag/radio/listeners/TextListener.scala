package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

trait MessageTextFormat {
  def format(message: Message, builder: StringBuilder) {
    builder.append(format(message))
  }

  def format(message: Message): String = {
    val sb = new StringBuilder("")
    format(message, sb)
    sb.result()
  }

  def ++(more: MessageTextFormat) = new MessageTextFormat.Concat(this, more)

  def trim = new MessageTextFormat.Trim(this)
}

object MessageTextFormat {
  implicit class Const(v: String) extends MessageTextFormat {
    override def format(message: Message) = v
  }

  object Empty extends MessageTextFormat {
    override def format(message: Message, sb: StringBuilder) {}
  }

  implicit class Fn(f: Message => String) extends MessageTextFormat {
    override def format(message: Message) = f(message)
  }

  class Concat(m1: MessageTextFormat, m2: MessageTextFormat) extends MessageTextFormat {
    override def format(message: Message, sb: StringBuilder) {
      m1.format(message, sb)
      sb.append(" ")
      m2.format(message, sb)
    }
  }

  trait Deferred extends MessageTextFormat {
    def format: MessageTextFormat
    override def format(message: Message, builder: StringBuilder) = format.format(message, builder)
  }

  class Trim(m: MessageTextFormat) extends MessageTextFormat {
    override def format(message: Message, builder: StringBuilder) {
      builder.append(m.format(message).trim)
    }
  }

  object MessageText extends Fn(_.text)

  class Time(format: java.time.format.DateTimeFormatter, timeZoneId: java.time.ZoneId) extends MessageTextFormat {
    override def format(message: Message) = format.format(message.time.atZone(timeZoneId))
  }

  class LocalTime(format: java.time.format.DateTimeFormatter) extends Time(format, java.time.ZoneId.systemDefault())

  object LocalISODateTime extends LocalTime(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
  object LocalISODate extends LocalTime(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
  object LocalISOTime extends LocalTime(java.time.format.DateTimeFormatter.ISO_LOCAL_TIME)

  object AudienceAbrv extends MessageTextFormat {
    override def format(message: Message) = {
      (message.messageType match {
        case MessageType.Event(audience, _) => audience
        case MessageType.Metric(audience) => audience
      }) match {
        case Audience.Developer => "dev"
        case Audience.User => "usr"
      }
    }
  }

  object ComponentPathAsPackage extends MessageTextFormat {
    override def format(message: Message, sb: StringBuilder) {
      var first = true
      message.componentPath.foreach { c: String =>
        if (first) first = false else sb.append(".")
        sb.append(c)
      }
    }
  }

  object ExceptionMessage extends MessageTextFormat

  object ContextJson extends MessageTextFormat {
    private final def jsonEscape(v: String): String = {
      // FIXME: also all chars < 20 => \u0019 - or use a json lib
      "\"" + v.replaceAllLiterally("\\", "\\\\").replaceAllLiterally("\"", "\\\"")
    }

    private final def cval(c: ContextValue): String = c match {
      case ContextBoolean(true) => "true"
      case ContextBoolean(false) => "false"
      case ContextString(v) => jsonEscape(v)
      case ContextInteger(v) => v.toString
    }

    override def format(message: Message): String = {
      val ctx_str = message.context.map { p => s"${p._1}: ${cval(p._2)}" }.mkString(", ")
      s"{$ctx_str}"
    }
  }

}

abstract class TextListener(format: MessageTextFormat) extends Listener {
  def process(message: Message) = process(format.format(message))

  def process(message: String)
}

