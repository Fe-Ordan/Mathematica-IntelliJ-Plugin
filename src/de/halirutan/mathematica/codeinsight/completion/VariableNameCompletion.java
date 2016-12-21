/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.codeinsight.completion;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.hash.HashSet;
import com.intellij.util.indexing.FileBasedIndex;
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex;
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex.Key;
import de.halirutan.mathematica.index.packageexport.PackageExportSymbol;
import de.halirutan.mathematica.parsing.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * @author patrick (4/2/13)
 */
public class VariableNameCompletion extends MathematicaCompletionProvider {

  private static final double LOCAL_VARIABLE_PRIORITY = 3000;
  private static final double GLOBAL_VARIABLE_PRIORITY = 2500;
  private static final Set<String> NAMES = SymbolInformationProvider.getSymbolNames().keySet();

  @Override
  void addTo(CompletionContributor contributor) {
    final PsiElementPattern.Capture<PsiElement> symbolPattern = PlatformPatterns.psiElement().withParent(Symbol.class);
    contributor.extend(CompletionType.BASIC, symbolPattern, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final Symbol callingSymbol = (Symbol) parameters.getPosition().getParent();
    final String callingSymbolName = callingSymbol.getSymbolName();

    if (!parameters.isExtendedCompletion()) {
      final PsiFile containingFile = parameters.getOriginalFile();
      List<Symbol> variants = Lists.newArrayList();

      GlobalDefinitionCompletionProvider visitor = new GlobalDefinitionCompletionProvider();
      containingFile.accept(visitor);
      for (String name : visitor.getFunctionsNames()) {
        if (!NAMES.contains(name)) {
          result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(name), GLOBAL_VARIABLE_PRIORITY));
        }
      }

      ImportedContextVisitor importVisitor = new ImportedContextVisitor();
      callingSymbol.getContainingFile().accept(importVisitor);
      final java.util.HashSet<String> importedContexts = importVisitor.getImportedContexts();
      final FileBasedIndex index = FileBasedIndex.getInstance();
      final Collection<Key> allKeys = index.getAllKeys(MathematicaPackageExportIndex.INDEX_ID, callingSymbol.getProject());
      for (Key key : allKeys) {
        final List<List<PackageExportSymbol>> values = index.getValues(MathematicaPackageExportIndex.INDEX_ID, key, GlobalSearchScope.allScope(callingSymbol.getProject()));
        for (List<PackageExportSymbol> value : values) {
          for (PackageExportSymbol packageExportSymbol : value) {
            //TODO: Implement adding completions
//            if(packageExportSymbol.nameSpace.equals())
          }
        }
      }


      final LocalDefinitionCompletionProvider processor = new LocalDefinitionCompletionProvider(callingSymbol);
      PsiTreeUtil.treeWalkUp(processor, callingSymbol, containingFile, ResolveState.initial());

      variants.addAll(processor.getSymbols());


      for (Symbol currentSymbol : variants) {
        if (!NAMES.contains(currentSymbol.getSymbolName())) {
          result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(currentSymbol), LOCAL_VARIABLE_PRIORITY));
        }
      }
    } else {
      final Set<String> allSymbols = new HashSet<String>();
      PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
        @Override
        public void visitElement(PsiElement element) {
          if (element instanceof Symbol && !callingSymbolName.equals(((Symbol) element).getSymbolName() + "ZZZ")) {
            allSymbols.add(((Symbol) element).getSymbolName());
          }
          element.acceptChildren(this);
        }
      };
      visitor.visitFile(parameters.getOriginalFile());
      for (String currentSymbol : allSymbols) {
        if (!NAMES.contains(currentSymbol)) {
          result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(currentSymbol), GLOBAL_VARIABLE_PRIORITY));
        }
      }

    }
  }


}
