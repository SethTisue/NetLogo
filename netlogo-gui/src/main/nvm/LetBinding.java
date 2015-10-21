// (C) Uri Wilensky. https://github.com/NetLogo/NetLogo

package org.nlogo.nvm;

import org.nlogo.core.Let;

strictfp class LetBinding {
  final Let let;
  Object value;

  LetBinding(Let let, Object value) {
    this.let = let;
    this.value = value;
  }
}
