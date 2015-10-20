// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.compiler

import scala.collection.immutable.ListMap
import org.nlogo.core.{ Instantiator, Program, BreedIdentifierHandler }
import org.nlogo.{ api, core, nvm, prim }

class Backifier(program: Program,
  extensionManager: core.ExtensionManager,
  procedures: ListMap[String, nvm.Procedure]) {

  val replacements = Map[String, String](
    "org.nlogo.prim.etc._plus"            -> "org.nlogo.prim._plus",
    "org.nlogo.prim.etc._links"           -> "org.nlogo.prim._links",
    "org.nlogo.prim.etc._patch"           -> "org.nlogo.prim._patch",
    "org.nlogo.prim.etc._breedhere"       -> "org.nlogo.prim._breedhere",
    "org.nlogo.prim.etc._breedsingular"   -> "org.nlogo.prim._breedsingular",
    "org.nlogo.prim.etc._createlinkwith"  -> "org.nlogo.prim._createlinkwith",
    "org.nlogo.prim.etc._fileexists"      -> "org.nlogo.prim.file._fileexists",
    "org.nlogo.prim.etc._filedelete"      -> "org.nlogo.prim.file._filedelete",
    "org.nlogo.prim.etc._fileopen"        -> "org.nlogo.prim.file._fileopen",
    "org.nlogo.prim.etc._filewrite"       -> "org.nlogo.prim.file._filewrite",
    "org.nlogo.prim.etc._fileprint"       -> "org.nlogo.prim.file._fileprint",
    "org.nlogo.prim.etc._fileclose"       -> "org.nlogo.prim.file._fileclose",
    "org.nlogo.prim.etc._fileread"        -> "org.nlogo.prim.file._fileread",
    "org.nlogo.prim.etc._filereadline"    -> "org.nlogo.prim.file._filereadline",
    "org.nlogo.prim.etc._clearallplots"   -> "org.nlogo.prim.plot._clearallplots",
    "org.nlogo.prim.etc._updateplots"     -> "org.nlogo.prim.plot._updateplots",
    "org.nlogo.prim.etc._plot"            -> "org.nlogo.prim.plot._plot",
    "org.nlogo.prim.etc._setupplots"      -> "org.nlogo.prim.plot._setupplots",
    "org.nlogo.prim.etc._createlinkswith" -> "org.nlogo.prim._createlinkswith"
  )

  private def backifyName(name: String): String = {
    val alteredName = name.replaceFirst("\\.core\\.", ".")
    if (replacements.contains(alteredName))
      replacements(alteredName)
    else
      alteredName
  }

  private def fallback[T1 <: core.Instruction, T2 <: nvm.Instruction](i: T1): T2 =
    BreedIdentifierHandler.process(i.token.copy(value = i.token.text.toUpperCase), program) match {
      case None =>
        Instantiator.newInstance[T2](
          Class.forName(backifyName(i.getClass.getName)))
      case Some((className, breedName, _)) =>
        val name = "org.nlogo.prim." + className
        val primName = if (replacements.contains(name)) replacements(name) else name
        Instantiator.newInstance[T2](
          Class.forName(primName), breedName)
    }

  def apply(c: core.Command): nvm.Command = {
    val result: nvm.Command = c match {
      case core.prim._extern(_) =>
        new prim._extern(
          extensionManager.replaceIdentifier(c.token.text.toUpperCase)
            .asInstanceOf[api.Command])
      case core.prim._call(proc) =>
        new prim._call(procedures(proc.name))
      case core.prim._let(let) =>
        // this probably won't work :P
        val newLet = new org.nlogo.api.Let(let.name, c.token.start, c.token.end)
        val l = new prim._let()
        l.let = newLet
        l
      case cc: core.prim._carefully =>
        // this probably won't work :P
        new prim._carefully()
      case _ =>
        fallback[core.Command, nvm.Command](c)
    }
    result.token_=(c.token)
    result.agentClassString = c.agentClassString
    result
  }

  def apply(r: core.Reporter): nvm.Reporter = {
    val result: nvm.Reporter = r match {

      case core.prim._letvariable(let) =>
        // this probably won't work :P
        val newLet = new org.nlogo.api.Let(let.name, r.token.start, r.token.end)
        new prim._letvariable(newLet, let.name)

      case core.prim._const(value) =>
        value match {
          case d: java.lang.Double   => new prim._constdouble(d)
          case b: java.lang.Boolean  => new prim._constboolean(b)
          case l: core.LogoList      =>
            val ll = api.LogoList(l: _*)
            new prim._constlist(ll)
          case core.Nobody           => new prim._nobody()
          case s: String             => new prim._conststring(s)
        }

      case core.prim._commandtask(argcount) =>
        // this won't work until we have LambdaLifter :P
        new prim._commandtask(null)  // LambdaLifter will fill in

      case core.prim._reportertask(argcount) =>
        new prim._reportertask()
        // new prim._reportertask(argcount)

      case core.prim._externreport(_) =>
        new prim._externreport(
          extensionManager.replaceIdentifier(r.token.text.toUpperCase)
            .asInstanceOf[api.Reporter])

      case core.prim._breedvariable(varName) =>
        new prim._breedvariable(varName)
      case core.prim._linkbreedvariable(varName) =>
        new prim._linkbreedvariable(varName)

      case core.prim._procedurevariable(vn, name) =>
        new prim._procedurevariable(vn, name)
      case core.prim._taskvariable(vn) =>
        new prim._taskvariable(vn)

      case core.prim._observervariable(vn) =>
        new prim._observervariable(vn)
      case core.prim._turtlevariable(vn) =>
        new prim._turtlevariable(vn)
      case core.prim._linkvariable(vn) =>
        new prim._linkvariable(vn)
      case core.prim._patchvariable(vn) =>
        new prim._patchvariable(vn)
      case core.prim._turtleorlinkvariable(varName) =>
        new prim._turtleorlinkvariable(varName)

      case core.prim._callreport(proc) =>
        new prim._callreport(procedures(proc.name))

      case core.prim._errormessage(Some(let)) =>
        // this probably won't work :P
        // new prim._errormessage(let)
        new prim._errormessage()
      case core.prim._errormessage(None) =>
        throw new Exception("Parse error - errormessage not matched with carefully")

      // diabolical special case: if we have e.g. `breed [fish]` with no singular,
      // then the singular defaults to `turtle`, which will cause BreedIdentifierHandler
      // to interpret "turtle" as _breedsingular - ST 4/12/14
      case core.prim._turtle() =>
        new prim._turtle()

      case _ =>
        fallback[core.Reporter, nvm.Reporter](r)

    }
    result.token_=(r.token)
    result.agentClassString = r.agentClassString
    result
  }
}
