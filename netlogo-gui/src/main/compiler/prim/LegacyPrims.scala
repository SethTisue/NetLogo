// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.compiler.prim

import org.nlogo.core.{ Command, Reporter, Syntax },
  Syntax.{ AgentType, AgentsetType, NumberType, PatchType, StringType, TurtleType }

// NOTE: These prims are NOT specific to the GUI as such, but rather are
// primitives which are not in NLW at this time. For use with the NetLogoGUI dialect

case class _behaviorspaceexperimentname() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = StringType)
}

case class _behaviorspacerunnumber() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = NumberType)
}

case class _distancenowrap() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = NumberType, agentClassString = "-TP-", right = List(PatchType | TurtleType))
}

case class _distancexynowrap() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = NumberType, agentClassString = "-TP-", right = List(NumberType, NumberType))
}

case class _inconenowrap() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = AgentsetType, left = AgentsetType, agentClassString = "-T--", precedence = 12, right = List(NumberType, NumberType))
}

case class _inradiusnowrap() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = AgentsetType, left = AgentsetType, agentClassString = "-TP-", precedence = 12, right = List(NumberType))
}

case class _towardsnowrap() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = NumberType, agentClassString = "-TP-", right = List(PatchType | TurtleType))
}

case class _towardsxynowrap() extends Reporter {
  def syntax = Syntax.reporterSyntax(ret = NumberType, agentClassString = "-TP-", right = List(NumberType, NumberType))
}

package gui {
  case class _beep() extends Command {
    def syntax = Syntax.commandSyntax()
  }
}

case class _facenowrap() extends Command {
  def syntax = Syntax.commandSyntax(agentClassString = "-T--", right = List(AgentType))
}

case class _facexynowrap() extends Command {
  def syntax = Syntax.commandSyntax(agentClassString = "-T--", right = List(NumberType, NumberType))
}
