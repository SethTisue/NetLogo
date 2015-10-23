// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.api

import org.nlogo.core.{ AgentVariableSet, Dialect, TokenMapperInterface => CoreTokenMapperInterface,
Resource, Command => CoreCommand, Reporter => CoreReporter }

object ThreeDProgram extends Dialect {
  val is3D = true;
  val agentVariables = new AgentVariableSet {
    val getImplicitObserverVariables: Seq[String] = Seq()
    val getImplicitTurtleVariables: Seq[String]   = AgentVariables.getImplicitTurtleVariables(true)
    val getImplicitPatchVariables: Seq[String]    = AgentVariables.getImplicitPatchVariables(true)
    val getImplicitLinkVariables: Seq[String]     = AgentVariables.getImplicitLinkVariables
  }
  val tokenMapper = ThreeDTokenMapper
}

object ThreeDTokenMapper extends CoreTokenMapperInterface {
  val defaultMapper = org.nlogo.core.DefaultTokenMapper
  val path = "/system/tokens-threed.txt"

  val pkgName = "org.nlogo.compiler.prim"

  private def entries(entryType: String): Iterator[(String, String)] =
    for {
      line <- Resource.lines(path)
      if !line.startsWith("#")
      Array(tpe, primName, className) = line.split(" ")
      if tpe == entryType
    } yield primName.toUpperCase -> (s"$pkgName.$className")

  lazy val commands  = entries("C").toMap
  lazy val reporters = entries("R").toMap

  def allCommandNames: Set[String]  = commands.keySet
  def allReporterNames: Set[String] = reporters.keySet

  private def instantiate[T](name: String) =
    Class.forName(name).newInstance.asInstanceOf[T]

  def getCommand(s: String): Option[CoreCommand] =
    commands.get(s.toUpperCase).map(instantiate[CoreCommand]) orElse
      defaultMapper.getCommand(s)

  def getReporter(s: String): Option[CoreReporter] =
    reporters.get(s.toUpperCase).map(instantiate[CoreReporter]) orElse
      defaultMapper.getReporter(s)
}
