// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.compiler
import org.nlogo.agent.AgentSet

import org.nlogo.core.Program
import org.nlogo.core.Token
import org.nlogo.core.TokenType
import org.nlogo.nvm.Instruction
// The Helper class and some of the methods aren't private because we want to get at them from
// TestBreedIdentifierHandler. - ST 12/22/08
private object BreedIdentifierHandler {
  import org.nlogo.prim._
  import TokenType.Command
  import TokenType.Reporter
  def process(token:Token,program:Program):Option[Token] = {
    val handlers = if(program.is3D) handlers3D else handlers2D
    handlers.toStream.flatMap(_.process(token,program)).headOption
  }
  def turtle(patternString:String,tokenType:TokenType,singular:Boolean,primClass:Class[_ <: Instruction]) =
    new Helper(patternString,tokenType,singular,primClass,
               _.breeds, _.breedsSingular, (obj:AnyRef) => true)
  def directedLink(patternString:String,tokenType:TokenType,singular:Boolean,primClass:Class[_ <: Instruction]) =
    new Helper(patternString,tokenType,singular,primClass,
               _.linkBreeds, _.linkBreedsSingular,
               { case a:AgentSet => a.isDirected
                case s:String => s == "DIRECTED-LINK-BREED" } )
  def undirectedLink(patternString:String,tokenType:TokenType,singular:Boolean,primClass:Class[_ <: Instruction]) =
    new Helper(patternString,tokenType,singular,primClass,
               _.linkBreeds, _.linkBreedsSingular,
               { case a:AgentSet => a.isUndirected
                case s:String => s == "UNDIRECTED-LINK-BREED" } )
  private val handlers2D = handlers(false)
  private val handlers3D = handlers(true)
  private def handlers(is3D:Boolean) = List(
    // prims for turtle breeds
    turtle("CREATE-*", Command, false, classOf[_createturtles]),
    turtle("CREATE-ORDERED-*", Command, false, classOf[_createorderedturtles]),
    turtle("HATCH-*", Command, false, classOf[_hatch]),
    turtle("SPROUT-*", Command, false,classOf[_sprout]),
    turtle("IS-*?", Reporter, true,classOf[_isbreed]),
    turtle("*-HERE", Reporter, false,classOf[_breedhere]),
    turtle("*-ON", Reporter, false,classOf[_breedon]),
    turtle("*", Reporter, false, classOf[_breed]),
    turtle("*", Reporter, true, classOf[_breedsingular]),
    // if we're in 3D point to the 3D version since
    // the syntax is different in 3D ev 12/11/06
    turtle("*-AT", Reporter, false,
           if(is3D) classOf[org.nlogo.prim.threed._breedat]
           else classOf[_breedat]),
    // prims for link breeds
    directedLink("*", Reporter, true, classOf[_linkbreedsingular]),
    undirectedLink("*", Reporter, true, classOf[_linkbreedsingular]),
    directedLink("*", Reporter, false, classOf[_linkbreed]),
    undirectedLink("*", Reporter, false, classOf[_linkbreed]),
    directedLink("IS-*?", Reporter, true,classOf[_isbreed]),
    undirectedLink("IS-*?", Reporter, true,classOf[_isbreed]),
    directedLink("CREATE-*-FROM", Command, true,classOf[_createlinkfrom]),
    directedLink("CREATE-*-FROM", Command, false,classOf[_createlinksfrom]),
    directedLink("CREATE-*-TO", Command, true,classOf[_createlinkto]),
    directedLink("CREATE-*-TO", Command, false,classOf[_createlinksto]),
    undirectedLink("CREATE-*-WITH", Command, true,classOf[_createlinkwith]),
    undirectedLink("CREATE-*-WITH", Command, false,classOf[_createlinkswith]),
    directedLink("IN-*-NEIGHBOR?", Reporter, true,classOf[_inlinkneighbor]),
    directedLink("OUT-*-NEIGHBOR?", Reporter, true,classOf[_outlinkneighbor]),
    directedLink("IN-*-FROM", Reporter, true,classOf[_inlinkfrom]),
    directedLink("OUT-*-TO", Reporter, true,classOf[_outlinkto]),
    directedLink("OUT-*-NEIGHBORS", Reporter, true,classOf[_outlinkneighbors]),
    directedLink("IN-*-NEIGHBORS", Reporter, true,classOf[_inlinkneighbors]),
    directedLink("MY-IN-*", Reporter, false,classOf[_myinlinks]),
    directedLink("MY-OUT-*", Reporter, false,classOf[_myoutlinks]),
    undirectedLink("*-NEIGHBORS", Reporter, true,classOf[_linkneighbors]),
    undirectedLink("MY-*", Reporter, false,classOf[_mylinks]),
    undirectedLink("*-WITH", Reporter, true,classOf[_linkwith]),
    undirectedLink("*-NEIGHBOR?", Reporter, true,classOf[_linkneighbor])
  )
  class Helper
    (patternString:String,tokenType:TokenType,singular:Boolean,primClass:Class[_ <: Instruction],
     breeds:(Program)=>java.util.Map[String,Object],singularMap:(Program)=>java.util.Map[String,String],
     isValidBreed:(AnyRef)=>Boolean)
  {
    import java.util.regex.Pattern
    val pattern = Pattern.compile("\\A"+patternString.replaceAll("\\?","\\\\?").replaceAll("\\*","(.+)")+"\\Z")
    def process(tok:Token,program:Program):Option[Token] = {
      val matcher = pattern.matcher(tok.value.asInstanceOf[String])
      if(!matcher.matches()) return None
      val name = matcher.group(1)
      val map = if(singular) singularMap(program) else breeds(program)
      if(!map.containsKey(name)) return None
      val breedName = if(singular) map.get(name) else name
      if(!isValidBreed(breeds(program).get(breedName))) return None
      val instr = Instantiator.newInstance[Instruction](primClass,breedName)
      val tok2 = new Token(tok.value.asInstanceOf[String],tokenType,instr)(tok.start,tok.end,tok.filename)
      instr.token_=(tok2)
      Some(tok2)
    }
  }
}
