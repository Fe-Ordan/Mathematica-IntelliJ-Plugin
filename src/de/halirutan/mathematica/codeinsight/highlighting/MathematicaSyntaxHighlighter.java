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

package de.halirutan.mathematica.codeinsight.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import de.halirutan.mathematica.lang.lexer.MathematicaLexer;
import de.halirutan.mathematica.lang.parsing.MathematicaElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a syntax highlighter for the Mathematica language. This class is registered through {@link
 * MathematicaSyntaxHighlighterFactory} so it can be used in the custom language plugin.
 *
 * @author patrick (1/3/13)
 */
public class MathematicaSyntaxHighlighter extends SyntaxHighlighterBase {

  private static final Map<IElementType, TextAttributesKey> colors = new HashMap<>();
  private final MathematicaLexer myLexer;

  public MathematicaSyntaxHighlighter() {
    myLexer = new MathematicaLexer();

    fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL);
    fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_NAMED_CHARACTER);
    fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL_BEGIN);
    fillMap(colors, MathematicaSyntaxHighlighterColors.STRING, MathematicaElementTypes.STRING_LITERAL_END);
    fillMap(colors, MathematicaSyntaxHighlighterColors.IDENTIFIER, MathematicaElementTypes.IDENTIFIER);
    fillMap(colors, MathematicaElementTypes.OPERATORS, MathematicaSyntaxHighlighterColors.OPERATORS);
    fillMap(colors, MathematicaElementTypes.BRACES, MathematicaSyntaxHighlighterColors.BRACE);
    fillMap(colors, MathematicaElementTypes.LITERALS, MathematicaSyntaxHighlighterColors.LITERAL);
    fillMap(colors, MathematicaElementTypes.COMMENTS, MathematicaSyntaxHighlighterColors.COMMENT);
//    fillMap(colors, MathematicaElementTypes.SLOTS, MathematicaSyntaxHighlighterColors.PATTERN);

    fillMap(colors, MathematicaSyntaxHighlighterColors.BAD_CHARACTER, MathematicaElementTypes.BAD_CHARACTER);
  }


  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return myLexer;
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(colors.get(tokenType));
  }

}
