// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.lex

import org.nlogo.api.{ CompilerException, TokenizerInterface }
import org.nlogo.core.Token
import org.nlogo.core.TokenType
import org.nlogo.core.ExtensionManager

object Tokenizer2D extends Tokenizer(TokenMapper2D)
object Tokenizer3D extends Tokenizer(TokenMapper3D)

class Tokenizer(tokenMapper: TokenMapper) extends TokenizerInterface {

  // this method never throws CompilerException, but use TokenType.Bad
  // instead, because if there's an error we want to still
  // keep going, so that findProcedurePositions and AutoConverter won't
  // be useless even if there's a tokenization error in the code
  // - ST 4/21/03, 5/24/03, 6/29/06
  def tokenizeRobustly(source: String): Seq[Token] =
    doTokenize(source, false, false, "", false)

  // for use by AutoConverter with org.nlogo.prim.dead - ST 2/20/08
  def tokenizeAllowingRemovedPrims(source: String): Seq[Token] =
    doTokenize(source, false, true, "", false);

  // and here's versions that throw CompilerException as soon as they hit a bad token - ST 2/20/08
  def tokenize(source: String): Seq[Token] =
    tokenize(source, "")
  def tokenize(source: String, filename: String): Seq[Token] = {
    val result = doTokenize(source, false, false, filename, true)
    result.find(_.tpe == TokenType.Bad) match {
      case Some(badToken) => throw new CompilerException(badToken)
      case None => result
    }
  }

  // this is used e.g. when colorizing
  private def tokenizeIncludingComments(source: String): Seq[Token] =
    doTokenize(source, true, false, "", false)

  // includeCommentTokens is used for syntax highlighting in the editor; allowRemovedPrimitives is
  // used when doing auto-conversions that require the parser - ST 7/7/06
  private def doTokenize(source: String, includeCommentTokens: Boolean, allowRemovedPrimitives: Boolean,
                         filename: String, stopAtFirstBadToken: Boolean): Seq[Token] =
  {
    val yy = new TokenLexerJ(
      new java.io.StringReader(source), tokenMapper, filename, allowRemovedPrimitives)
    val eof = new Token("", TokenType.Eof, "")(0, 0, "")
    def yystream: Stream[Token] = {
      val t = yy.yylex()
      if (t == null)
        Stream(eof)
      else if (stopAtFirstBadToken && t.tpe == TokenType.Bad)
        Stream(t, eof)
      else
        Stream.cons(t, yystream)
    }
    yystream.filter(includeCommentTokens || _.tpe != TokenType.Comment).toList
  }

  def nextToken(reader: java.io.BufferedReader): Token =
    new TokenLexerJ(reader, tokenMapper, null, false).yylex()

  def getTokenAtPosition(source: String, position: Int): Token = {
    // if the cursor is between two adjacent tokens we'll need to pick the token
    // the user probably wants for F1 purposes. see bug #139 - ST 5/2/12
    val interestingTokenTypes =
      List(TokenType.Literal, TokenType.Ident, TokenType.Command, TokenType.Reporter,
           TokenType.Keyword, TokenType.Ident)
    val candidates =
      tokenizeIncludingComments(source)
        .dropWhile(_.end < position)
        .takeWhile(_.start <= position)
        .take(2) // be robust against Eof tokens, etc.
    candidates match {
      case Seq() => null
      case Seq(t) => t
      case Seq(t1, t2) =>
        if (interestingTokenTypes.contains(t2.tpe))
          t2 else t1
    }
  }

  def isValidIdentifier(ident: String): Boolean =
    tokenizeRobustly(ident) match {
      case Seq(
        Token(n, TokenType.Ident, _),
        Token(_, TokenType.Eof, _)) =>
        ! (tokenMapper.isCommand(n.toUpperCase) ||
          tokenMapper.isReporter(n.toUpperCase) ||
          tokenMapper.isVariable(n.toUpperCase))
      case _ => false
    }

  // this is for the syntax-highlighting editor in the HubNet client, where we don't have
  // an extension manager.
  def tokenizeForColorization(source: String): Array[Token] =
    tokenizeIncludingComments(source).takeWhile(_.tpe != TokenType.Eof).toArray

  // this is for the syntax-highlighting editor
  def tokenizeForColorization(source: String, extensionManager: ExtensionManager): Array[Token] = {
    // In order for extension primitives to be the right color, we need to change
    // the type of the token from TokenType.Ident to TokenType.Command or TokenType.Reporter
    // if the identifier is recognized by the extension.
    def replaceImports(token: Token): Token =
      if (!extensionManager.anyExtensionsLoaded || token.tpe != TokenType.Ident)
        token
      // look up the replacement.
      else extensionManager.replaceIdentifier(token.value.asInstanceOf[String]) match {
        case null => token
        case prim =>
          val newType =
            if (prim.isInstanceOf[org.nlogo.api.Command])
              TokenType.Command
            else TokenType.Reporter
          new Token(token.text, newType, token.value)(
            token.start, token.end, token.filename)
      }
    tokenizeForColorization(source).map(replaceImports)
  }

  def checkInstructionMaps() { tokenMapper.checkInstructionMaps() }

}
