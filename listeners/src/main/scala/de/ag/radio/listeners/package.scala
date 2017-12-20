package de.ag.radio

package object listeners {
  /**
    * Returns a Transmitter, that is attached to the given listener.
    */
  implicit def attach(listener: Listener): Transmitter = Connector(listener)
}
