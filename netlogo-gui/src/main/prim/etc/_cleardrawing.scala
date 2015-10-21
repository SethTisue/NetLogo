// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.prim.etc

import org.nlogo.api.Syntax
import org.nlogo.nvm.{ Command, Context }

class _cleardrawing extends Command {
  override def syntax =
    Syntax.commandSyntax("O---")

  switches = true
  override def perform(context: Context) {
    workspace.clearDrawing()
    context.ip = next
  }
}
