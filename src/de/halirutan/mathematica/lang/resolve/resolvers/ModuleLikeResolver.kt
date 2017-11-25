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

package de.halirutan.mathematica.lang.resolve.resolvers

import com.intellij.psi.ResolveState
import de.halirutan.mathematica.lang.psi.LocalizationConstruct
import de.halirutan.mathematica.lang.psi.api.FunctionCall
import de.halirutan.mathematica.lang.psi.api.Symbol
import de.halirutan.mathematica.lang.psi.api.lists.MList
import de.halirutan.mathematica.lang.resolve.SymbolResolveHint
import de.halirutan.mathematica.lang.resolve.SymbolResolveResult

/**  This extracts the local defined arguments of a `Module`, `Block`, ... call. Examples are
 *   `Module[{arg}, arg^2]` extracts `arg`  * `With[{a=1,b=2}, a*b]` extracts `a,b`
 * */
class ModuleLikeResolver : Resolver {

  override fun resolve(symbol: Symbol, scopingElement: FunctionCall, state: ResolveState): SymbolResolveResult? {
    val lastParent = state.get(SymbolResolveHint.LAST_PARENT)
    val scope = scopingElement.scopingConstruct
    val parameters = scopingElement.parameters

    // when resolving doesn't make sense
    if (!scopingElement.isScopingConstruct || !LocalizationConstruct.isModuleLike(scope) || parameters.size < 2) {
      return null
    }

    val body = getBodyElement(parameters, scope) ?: return null
    val defLists = getLocalizationParameters(parameters, scope).takeUnless { it.isEmpty() } ?: return null

    // Symbol to resolve is located in the body of Module
    if (lastParent == body) {
      defLists
          .filterIsInstance<MList>()
          .map { it.listElements }
          .forEach { assignments ->
            assignments
                .map { findSymbolInAssignment(it) }
                .filterIsInstance<Symbol>()
                .forEach { s ->
                  if (s.fullSymbolName == symbol.fullSymbolName) {
                    return SymbolResolveResult(s, scope, scopingElement, true)
                  }
                }
          }
    } else { // Symbol appears in the Module definition list itself
      val index = defLists.indexOf(lastParent).takeUnless { it == -1 } ?: return null
      for (defList in defLists.subList(0, index + 1).filterIsInstance<MList>()) {
        defList.listElements
            .mapNotNull { listElement -> findSymbolInAssignment(listElement).takeIf { it is Symbol } }
            .filter { it.fullSymbolName == symbol.fullSymbolName }
            .forEach {
              // self-reference
              // in Module[{x=..},..} the x will point to itself
              if (it == symbol) {
                return SymbolResolveResult(it, scope, scopingElement, true)
              } else {
                if (lastParent != defList) {
                  return SymbolResolveResult(it, scope, scopingElement, true)
                }
              }
            }
      }

    }
    return null
  }
}
