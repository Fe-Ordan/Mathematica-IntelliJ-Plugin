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

package de.halirutan.mathematica.lang.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.ResolveCache.AbstractResolver;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex;
import de.halirutan.mathematica.index.packageexport.PackageExportSymbol;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.impl.LightBuiltInSymbol;
import de.halirutan.mathematica.lang.psi.impl.LightUndefinedSymbol;
import de.halirutan.mathematica.lang.psi.util.LocalizationConstruct.MScope;
import de.halirutan.mathematica.lang.resolve.processors.GlobalDefinitionResolveProcessor;
import de.halirutan.mathematica.lang.resolve.processors.LocalDefinitionResolveProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities.isBuiltInSymbol;

/**
 * @author patrick (08.07.17).
 */
public class MathematicaSymbolResolver implements AbstractResolver<Symbol, SymbolResolveResult> {


  @Override
  public SymbolResolveResult resolve(@NotNull Symbol ref, boolean incompleteCode) {

    if (isBuiltInSymbol(ref)){
      return new SymbolResolveResult(new LightBuiltInSymbol(ref), MScope.KERNEL_SCOPE, true);
    }

    LocalDefinitionResolveProcessor processor = new LocalDefinitionResolveProcessor(ref);
    PsiTreeUtil.treeWalkUp(processor, ref, ref.getContainingFile(), ResolveState.initial());
    final Symbol referringSymbol = processor.getMyReferringSymbol();
    if (referringSymbol != null) {
      return new SymbolResolveResult(referringSymbol, processor.getMyLocalization(), true);
    }

    GlobalDefinitionResolveProcessor globalProcessor = new GlobalDefinitionResolveProcessor(ref);
    PsiTreeUtil.processElements(ref.getContainingFile(), globalProcessor);


    final PsiElement globalDefinition = globalProcessor.getMyReferringSymbol();
    if (globalDefinition != null) {
      return new SymbolResolveResult(globalDefinition, MScope.FILE_SCOPE, true);
    }

    final FileBasedIndex fileIndex = FileBasedIndex.getInstance();
    final Project project = ref.getProject();
    final Collection<PackageExportSymbol> allKeys = fileIndex.getAllKeys(MathematicaPackageExportIndex.INDEX_ID, project);


    for (PackageExportSymbol key : allKeys) {
      if (key.isExported() && key.getSymbol().equals(ref.getSymbolName())) {
        final Collection<VirtualFile> containingFiles = fileIndex.getContainingFiles(MathematicaPackageExportIndex.INDEX_ID, key, GlobalSearchScope.allScope(project));
        if (containingFiles.size() > 0) {
          final VirtualFile file = containingFiles.iterator().next();
          final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
          if (psiFile != null) {
//            final PsiElement elementAt = psiFile.findElementAt(key.getOffset());
            final Symbol elementAt = PsiTreeUtil.findElementOfClassAtOffset(psiFile, key.getOffset(), Symbol.class, true);
            if (elementAt != null) {
              return new SymbolResolveResult(elementAt, MScope.FILE_SCOPE, true);
            }
            return new SymbolResolveResult(psiFile, MScope.FILE_SCOPE, true);
          }
        }
      }
    }

    return new SymbolResolveResult(new LightUndefinedSymbol(ref), MScope.NULL_SCOPE, true);

  }

}
