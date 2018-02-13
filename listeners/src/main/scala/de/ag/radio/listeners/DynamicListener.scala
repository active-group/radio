package de.ag.radio.listeners

import de.ag.radio._
import java.time.Instant

class DynamicListener(initial: Listener = DeafListener) extends Listener {
  import java.util.concurrent.atomic.AtomicReference

  private val base = new AtomicReference[Listener](initial)

  override def isListening(componentPath: List[String], messageType: MessageType) =
    base.get().isListening(componentPath, messageType)

  override def process(message: Message) = base.get().process(message)

  override def close() { base.get().close() }

  /*
   * Note: this class takes over the 'ownership' of the given listener, and closes it when appropiate.
   */
  def switchTo(newListener: Listener) {
    // Note: not thread safe with respect to other switchTo calls (but with respect to logging)
    val old = base.getAndSet(newListener)
    old.close()
  }
}
