/*
 * Copyright (c) 2017 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.halirutan.mathematica.lang.tree;

import com.intellij.core.CoreASTFactory;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.PsiCoreCommentImpl;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.lang.tree.impl.*;
import org.jetbrains.annotations.NotNull;

import static de.halirutan.mathematica.lang.parsing.MathematicaElementTypes.*;

/**
 * @author patrick (08.07.17).
 */
public class MathematicaASTFactory extends CoreASTFactory {

  @Override
  @NotNull
  public LeafElement createLeaf(@NotNull final IElementType type, @NotNull final CharSequence text) {
    if (type == IDENTIFIER) {
      return new PsiIdentifierImpl(text);
    } else if (type == NUMBER) {
      return new PsiNumberImpl(text);
    } else if (type == SLOT || type == SLOT_SEQUENCE) {
      return new PsiSlotImpl(text);
    } else if (type == SLOT_EXPRESSION) {
      return new PsiSlotExpressionImpl(text);
    } else if (type == STRINGIFIED_IDENTIFIER) {
      return new PsiStringifiedIdentifierImpl(text);
    } else if (type == COMMENT) {
      return new PsiCoreCommentImpl(COMMENT, text);
    } else if (OPERATORS.contains(type)) {
      return new PsiOperatorTokenImpl(type, text);
    }
    return new PsiMathematicaTokenImpl(type, text);
  }


}
