package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

private[listeners] class AConsoleListener(ansiColor: Boolean) extends Listener {
  import Console.{GREEN, RED, RESET, YELLOW}

  override def close() { }

  override def isListening(componentPath: List[String], mtype: MessageType) = true

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

  private val timeZoneId = java.time.ZoneId.systemDefault()

  override def process(componentPath: List[String], messageType: MessageType, message: => String, context: Map[String, ContextValue], exception: Option[Throwable], time: Instant) {
    val (color, audience) = messageType match {
      case MessageType.Event(audience, importance) => importance match {
        case Importance.Low => ("", audience)
        case Importance.High => (RED, audience)
      }
      case MessageType.Metric(audience) => (GREEN, audience)
    }
    val aud_str = audience match {
      case Audience.Developer => "developer"
      case Audience.User => "user"
    }
    val info = if (ansiColor) s"$RESET$color$aud_str$RESET" else aud_str
    val ctx_str = context.map { p => s"${p._1}: ${cval(p._2)}" }.mkString(", ")

    val t_str = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(time.atZone(timeZoneId))
    Console.println(s"${t_str} $info [${componentPath.mkString(" ")}] {$ctx_str} - ${message}${exception.map(" " + _.getMessage).getOrElse("")}")
  }
}

object ConsoleListener extends AConsoleListener(false)

object ColorConsoleListener extends AConsoleListener(true)
