// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.core

trait Dialect extends LowPriorityDialect {
  def is3D:           Boolean
  def agentVariables: AgentVariableSet
}

object Dialect extends LowPriorityDialect

trait LowPriorityDialect {
  implicit val dialect = NetLogoCore
}

case object NetLogoCore extends Dialect {
  val is3D = false
  val agentVariables = AgentVariables
}

trait AgentVariableSet {
  def getImplicitObserverVariables: Seq[String]
  def getImplicitTurtleVariables: Seq[String]
  def getImplicitPatchVariables: Seq[String]
  def getImplicitLinkVariables: Seq[String]
}
