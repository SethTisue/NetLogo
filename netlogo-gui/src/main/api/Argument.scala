// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.api

import org.nlogo.core.LogoList

/**
 * Interface provides access to arguments passed to the <code>perform</code> or <code>report</code>
 * methods of a primitive at run-time.
 *
 * <code>Arguments</code> are created by NetLogo and passed to the <code>perform</code> or
 * <code>report</code> methods of your primitive.
 *
 * @see Command#perform(Argument[], Context)
 * @see Reporter#report(Argument[], Context)
 */
trait Argument {

  /**
   * Returns the argument as an <code>Object</code> without type checking.
   *
   * @throws org.nlogo.api.ExtensionException
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def get: AnyRef

  /**
   * Returns the argument as an <code>org.nlogo.api.AgentSet</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not an <code>AgentSet</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getAgentSet: AgentSet

  /**
   * Returns the argument as an <code>Agent</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not an <code>Agent</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getAgent: Agent

  /**
   * Returns the argument as a boxed <code>java.lang.Boolean</code>
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>Boolean</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getBoolean: java.lang.Boolean

  /**
   * Returns the value of the argument as an unboxed <code>boolean</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>boolean</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getBooleanValue: Boolean

  /**
   * Returns the value of the argument as an unboxed <code>int</code>.
   * Any fractional part is discarded.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a number.
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getIntValue: Int

  /**
   * Returns the value of the argument as an unboxed <code>double</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a number.
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getDoubleValue: Double

  /**
   * Returns the argument as a <code>org.nlogo.core.LogoList</code>
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>LogoList</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getList: LogoList

  /**
   * Returns the argument as an <code>org.nlogo.api.Patch</code>
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>Patch</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getPatch: Patch

  /**
   * Returns the argument as a <code>String</code>
   *
   * @throws org.nlogo.api.ExtensionException if the argument cannot be cast to a <code>String</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getString: String

  /**
   * Returns the argument as a <code>org.nlogo.api.Turtle</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>Turtle</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getTurtle: Turtle

  /**
   * Returns the argument as a <code>org.nlogo.api.Link</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>Link</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getLink: Link

  /**
   * Returns the argument as a <code>org.nlogo.api.ReporterTask</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>ReporterTask</code>
   * @throws org.nlogo.api.LogoException if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getReporterTask: ReporterTask

  /**
   * Returns the argument as a <code>org.nlogo.api.CommandTask</code>.
   *
   * @throws org.nlogo.api.ExtensionException if the argument is not a <code>CommandTask</code>
   * @throws org.nlogo.api.LogoException      if a LogoException occurred while evaluating this argument
   */
  @throws(classOf[ExtensionException])
  @throws(classOf[LogoException])
  def getCommandTask: CommandTask

}
