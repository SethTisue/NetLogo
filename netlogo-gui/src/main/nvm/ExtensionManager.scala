// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.nvm

import org.nlogo.core.{ ExtensionManager => CoreExtensionManager }

trait ExtensionManager extends CoreExtensionManager {
  def dumpExtensionPrimitives: String
  def dumpExtensions: String
  def reset(): Unit
}
