// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.core

trait Dialect {
  def is3D: Boolean
}

case object NetLogoCore extends Dialect {
  val is3D = false
}
