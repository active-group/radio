package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

case class ConsoleTextFormat(ansiColor: Boolean) extends MessageTextFormat.Deferred {
  import ConsoleTextFormat._
  import MessageTextFormat._

  lazy val format = LocalISODate ++ LocalISOTime ++ (if (ansiColor) ColoredMessageType else SimpleMessageType) ++ ComponentPathAsPackage ++ (if (ansiColor) Blue(ContextJson) else ContextJson) ++ (MessageText ++ ExceptionMessage).trim
}

object ConsoleTextFormat {
  import MessageTextFormat._

  val SimpleMessageType = AudienceAbrv

  case class Blue(m: MessageTextFormat) extends MessageTextFormat {
    import Console.{BLUE, RESET}
    override def format(message: Message, sb: StringBuilder) {
      sb.append(RESET)
      sb.append(BLUE)
      m.format(message, sb)
      sb.append(RESET)
    }
  }

  object ColoredMessageType extends MessageTextFormat {
    import Console.{GREEN, RED, RESET, YELLOW}
    override def format(message: Message, sb: StringBuilder) {
      val color = message.messageType match {
        case MessageType.Event(audience, importance) => importance match {
          case Importance.Low => ""
          case Importance.High => RED
        }
        case MessageType.Metric(audience) => GREEN
      }
      sb.append(RESET)
      sb.append(color)
      AudienceAbrv.format(message, sb)
      sb.append(RESET)
    }
  }
}

case class ConsoleListener(format: MessageTextFormat) extends TextListener(format) {
  def close() {}

  def isListening(componentPath: List[String], messageType: de.ag.radio.MessageType) = true

  def process(message: String) = println(message)

  def withFormat(format: MessageTextFormat): ConsoleListener = new ConsoleListener(format)
}

object MonochromeConsoleListener extends ConsoleListener(ConsoleTextFormat(false))

object ColorConsoleListener extends ConsoleListener(ConsoleTextFormat(true))
