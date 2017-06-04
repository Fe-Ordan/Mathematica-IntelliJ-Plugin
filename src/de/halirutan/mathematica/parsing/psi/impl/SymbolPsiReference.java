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

package de.halirutan.mathematica.parsing.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import de.halirutan.mathematica.codeinsight.completion.SymbolInformationProvider;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import de.halirutan.mathematica.parsing.psi.util.GlobalDefinitionResolveProcessor;
import de.halirutan.mathematica.parsing.psi.util.LocalDefinitionResolveProcessor;
import de.halirutan.mathematica.parsing.psi.util.LocalizationConstruct.ConstructType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Provides functionality to resolve where a certain symbol is defined in code. For this, the SymbolPsiReference class
 * uses several processors which scan the local scope and global file scope. Note that GlobalDefinitionResolveProcessor
 * does not scan the whole file because this would be too slow. Instead, it expects that global symbol definitions are
 * done at file-scope. The class uses caching to speed up the resolve process. Once a definition for a symbol is found,
 * it is stored as long as the code in the concerning areas is not edited.
 *
 * @author patrick (5/8/13)
 */
public class SymbolPsiReference extends CachingReference implements PsiReference {

  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();
  private final Symbol myVariable;

  SymbolPsiReference(Symbol element) {
    super();
    myVariable = element;
  }

  public static boolean isBuiltInSymbol(PsiElement element) {
    if (element instanceof Symbol) {
      Symbol symbol = (Symbol) element;
      final String name = symbol.getMathematicaContext().equals("") ?
          "System`" + symbol.getSymbolName() : symbol.getFullSymbolName();
      return NAMES.contains(name);
    }
    return false;
  }

  @Nullable
  @Override
  public PsiElement resolveInner() {

    if (isBuiltInSymbol(myVariable)) return myVariable;

    if (myVariable.cachedResolve()) {
      if (myVariable.getFullSymbolName().equals(myVariable.getResolveElement().getFullSymbolName()) ||
          myVariable.getLocalizationConstruct().equals(ConstructType.ANONYMOUSFUNCTION)) {
        return myVariable.getResolveElement();
      } else {
        myVariable.subtreeChanged();
      }

    }

    LocalDefinitionResolveProcessor processor = new LocalDefinitionResolveProcessor(myVariable);
    PsiTreeUtil.treeWalkUp(processor, myVariable, myVariable.getContainingFile(), ResolveState.initial());
    final PsiElement referringSymbol = processor.getMyReferringSymbol();
    if (referringSymbol instanceof Symbol) {
      myVariable.setReferringElement((Symbol) referringSymbol, processor.getMyLocalization(), processor.getMyLocalizationSymbol());
      return referringSymbol;
    }

    GlobalDefinitionResolveProcessor globalProcessor = new GlobalDefinitionResolveProcessor(myVariable);
    PsiTreeUtil.processElements(myVariable.getContainingFile(), globalProcessor);


    final PsiElement globalDefinition = globalProcessor.getMyReferringSymbol();
    if (globalDefinition instanceof Symbol) {
      myVariable.setReferringElement((Symbol) globalDefinition, ConstructType.NULL, null);
      return globalDefinition;
    }

    return null;
  }

  @Override
  public Symbol getElement() {
    return myVariable;
  }

  @Override
  public TextRange getRangeInElement() {
    return TextRange.from(0, myVariable.getFirstChild().getNode().getTextLength());
  }

  @NotNull
  @Override
  public String getCanonicalText() {
    return myVariable.getFullSymbolName();
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    return myVariable.setName(newElementName);
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    if (isReferenceTo(element)) {
      return myVariable;
    }
    return handleElementRename(element.getText());
  }

  @Override
  public boolean isSoft() {
    return super.isSoft();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

}
