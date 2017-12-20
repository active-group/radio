package de.ag.radio

sealed trait ContextValue

case class ContextString(v: String) extends ContextValue
case class ContextInteger(v: Long) extends ContextValue
case class ContextBoolean(v: Boolean) extends ContextValue
