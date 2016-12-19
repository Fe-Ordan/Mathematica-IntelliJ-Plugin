/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.index.export;

import de.halirutan.mathematica.parsing.psi.MathematicaRecursiveVisitor;
import de.halirutan.mathematica.parsing.psi.api.Expression;
import de.halirutan.mathematica.parsing.psi.api.MessageName;
import de.halirutan.mathematica.parsing.psi.api.StringifiedSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author patrick (01.11.16).
 */
public class ExportSymbolVisitor extends MathematicaRecursiveVisitor {

  private List<PackageExportSymbol> myInfos;

  public ExportSymbolVisitor() {
    myInfos = new ArrayList<PackageExportSymbol>();
  }

  public List<PackageExportSymbol> getInfos() {
    return myInfos;
  }

  @Override
  public void visitMessageName(MessageName messageName) {
    final StringifiedSymbol tag = messageName.getTag();
    if (tag == null) {
      return;
    }
    if (tag.getText().equals("usage")) {
      final Expression symbol = messageName.getSymbol();
      if (symbol != null) {
        myInfos.add(new PackageExportSymbol("Global`", symbol.getName()));
      }

    }
  }
}
