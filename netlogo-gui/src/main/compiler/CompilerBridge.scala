// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.compiler

import org.nlogo.core.{ ExtensionManager, FrontEndProcedure, StructureResults }
import scala.collection.immutable.ListMap
import org.nlogo.{ core, api, nvm },
  nvm.Procedure

import scala.collection.JavaConverters._

object CompilerBridge {
  def apply(structureResults: StructureResults,
    extensionManager: ExtensionManager,
    oldProcedures: ListMap[String, Procedure],
    topLevelDefs: Seq[core.ProcedureDefinition]): Seq[ProcedureDefinition] = {
      val newProcedures = structureResults.procedures.map {
        case (k, p) => k -> fromApiProcedure(p)
      }
      val backifier = new Backifier(
        structureResults.program, extensionManager, oldProcedures ++ newProcedures)
      val astBackifier = new ASTBackifier(backifier)
      (newProcedures.values, topLevelDefs).zipped.map(astBackifier.backify).toSeq
  }

  private def fromApiProcedure(p: FrontEndProcedure): nvm.Procedure = {
    val proc = new nvm.Procedure(
      isReporter = p.isReporter,
      nameToken = p.nameToken,
      name = p.name,
      _displayName = if (p.displayName == "") None else Some(p.displayName),
      parent = null,
      argTokens = p.argTokens,
      initialArgs = p.args)
    proc.usableBy = p.agentClassString
    proc
  }
}
