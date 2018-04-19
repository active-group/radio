package de.ag.radio.listeners

import de.ag.radio._
import org.slf4j.{Logger, Marker, LoggerFactory, MarkerFactory, MDC}

sealed trait SLF4JLevel
object SLF4JLevel {
  case object Trace extends SLF4JLevel
  case object Debug extends SLF4JLevel
  case object Info extends SLF4JLevel
  case object Warn extends SLF4JLevel
  case object Error extends SLF4JLevel
}

case class SLF4JMarker(v: String, refs: Seq[SLF4JMarker] = Seq.empty) {
  def add(m: SLF4JMarker) = this.copy(refs = refs :+ m)
  def native: Marker = {
    val m = MarkerFactory.getMarker(v)
    refs.foreach { r => m.add(r.native) }
    m
  }
}

/**
  * A listener that passes messages on via the slf4j framework.
  * Note: the slf4j framework does not allow to pass a timestamp, so the original message time gets lost and is replaced by some 'current' time of logging.
  */
case class SLF4JListener(level: SLF4JLevel, marker: Option[SLF4JMarker] = None, format: MessageTextFormat = SLF4JListener.standardFormat, contextAsMDC: Boolean = true) extends Listener {
  def close() {}

  private def putContextToMDC(context: Map[String, ContextValue]) {
    context.foreach { case (k, v) =>
      v match {
        case ContextString(s) => MDC.put(k, s)
        case ContextInteger(i) => MDC.put(k, i.toString)
        case ContextBoolean(b) => MDC.put(k, b.toString)
      }
    }
  }

  private def maybeWithContextToMDC[A](context: Map[String, ContextValue])(f: => A): A = {
    if (contextAsMDC) {
      // Note: we keep things other may have put in.
      val prev = MDC.getCopyOfContextMap()
      putContextToMDC(context)
      try {
        f
      } finally {
        if (prev == null) MDC.clear() else MDC.setContextMap(prev)
      }
    } else {
      f
    }
  }

  private lazy val nativeMarker = marker.map(_.native)

  private def logger(componentPath: Seq[String]): Logger = LoggerFactory.getLogger(componentPath.mkString(".")) // TODO: cache some of these.

  def isListening(messageType: MessageType, componentPath: List[String], context: Map[String, ContextValue]) = {
    import SLF4JLevel._

    val l = logger(componentPath);
    (level, nativeMarker) match {
      case (Trace, None) => l.isTraceEnabled()
      case (Trace, Some(m)) => l.isTraceEnabled(m)
      case (Debug, None) => l.isDebugEnabled()
      case (Debug, Some(m)) => l.isDebugEnabled(m)
      case (Info, None) => l.isInfoEnabled()
      case (Info, Some(m)) => l.isInfoEnabled(m)
      case (Warn, None) => l.isWarnEnabled()
      case (Warn, Some(m)) => l.isWarnEnabled(m)
      case (Error, None) => l.isErrorEnabled()
      case (Error, Some(m)) => l.isErrorEnabled(m)
    }
  }

  def process(message: Message) = {
    import SLF4JLevel._
    maybeWithContextToMDC(message.context) {
      val l = logger(message.componentPath)
      val s = format.format(message);
      (level, nativeMarker, message.exception) match {
        case (Trace, None, None) => l.trace(s)
        case (Trace, Some(m), None) => l.trace(m, s)
        case (Trace, None, Some(e)) => l.trace(s, e)
        case (Trace, Some(m), Some(e)) => l.trace(m, s, e)
        case (Debug, None, None) => l.debug(s)
        case (Debug, Some(m), None) => l.debug(m, s)
        case (Debug, None, Some(e)) => l.debug(s, e)
        case (Debug, Some(m), Some(e)) => l.debug(m, s, e)
        case (Info, None, None) => l.info(s)
        case (Info, Some(m), None) => l.info(m, s)
        case (Info, None, Some(e)) => l.info(s, e)
        case (Info, Some(m), Some(e)) => l.info(m, s, e)
        case (Warn, None, None) => l.warn(s)
        case (Warn, Some(m), None) => l.warn(m, s)
        case (Warn, None, Some(e)) => l.warn(s, e)
        case (Warn, Some(m), Some(e)) => l.warn(m, s, e)
        case (Error, None, None) => l.error(s)
        case (Error, Some(m), None) => l.error(m, s)
        case (Error, None, Some(e)) => l.error(s, e)
        case (Error, Some(m), Some(e)) => l.error(m, s, e)
      }
    }
  }

  def withFormat(format: MessageTextFormat) = this.copy(format = format)
  def withLevel(level: SLF4JLevel) = this.copy(level = level)
}

object SLF4JListener {
  import MessageTextFormat._

  val standardFormat = MessageText

  val trace = SLF4JListener(SLF4JLevel.Trace)
  val debug = SLF4JListener(SLF4JLevel.Debug)
  val info = SLF4JListener(SLF4JLevel.Info)
  val warn = SLF4JListener(SLF4JLevel.Warn)
  val error = SLF4JListener(SLF4JLevel.Error)
}
