// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.api

import org.nlogo.core.Token
import org.nlogo.core.TokenType

/**
 * Exception thrown by various methods that accept NetLogo code as input and cause that code to be
 * compiled; indicates the code was invalid.  May be inspected to discover the location and nature
 * of the error.
 */
class CompilerException(message: String, val start: Int, val end: Int, val fileName: String)
extends Exception(message) {
  def this(token: Token) = this({assert(token.tpe == TokenType.Bad); token.value.asInstanceOf[String]},
                                 token.start, token.end, token.filename)

  override def toString =
    getMessage + " at position " + start + " in " + fileName
}
object CompilerException {
  val RuntimeErrorAtCompileTimePrefix = "Runtime error: "
}
