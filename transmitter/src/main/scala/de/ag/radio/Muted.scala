package de.ag.radio

object Muted extends Transmitter {
  override def hasListeners(mtype: MessageType): Boolean = false
  override def message(mtype: MessageType,msg: => String,exception: Option[Throwable],time: Option[java.time.Instant]): Unit = {}
  override def withContext(key: String,value: ContextValue): Transmitter = this
  override def within(component: String*): Transmitter = this
}
