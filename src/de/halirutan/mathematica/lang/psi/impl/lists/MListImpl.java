/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.lang.psi.impl.lists;

import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import de.halirutan.mathematica.lang.psi.MathematicaVisitor;
import de.halirutan.mathematica.lang.psi.api.Expression;
import de.halirutan.mathematica.lang.psi.api.lists.MList;
import de.halirutan.mathematica.lang.psi.impl.ExpressionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author patrick (4/14/13)
 */
public class MListImpl extends ExpressionImpl implements MList {

  private final static List<Expression> EMPTY = Lists.newArrayList();

  public MListImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MathematicaVisitor) {
      ((MathematicaVisitor) visitor).visitList(this);
    } else {
      super.accept(visitor);
    }
  }

  @Override
  public List<Expression> getListElements() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, Expression.class);
  }

  @Override
  public String toString() {
    return "List";
  }
}