/*
 * Copyright (c) 2015 Patrick Scheibe
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

package de.halirutan.mathematica.actions;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.BaseCodeInsightAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.halirutan.mathematica.codeinsight.folding.MathematicaCodeFoldingSettings;
import de.halirutan.mathematica.codeinsight.folding.MathematicaCodeFoldingSettingsImpl;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (16.08.15)
 */
public class ExpandNamedCharactersAction extends BaseCodeInsightAction {

  @NotNull
  @Override
  protected CodeInsightActionHandler getHandler() {
    return new CollapseExpandNamedCharactersHandler(true);
  }

  @Override
  public void update(final AnActionEvent event) {
    super.update(event);
    MathematicaCodeFoldingSettings settings = MathematicaCodeFoldingSettingsImpl.getInstance();
    event.getPresentation().setEnabled(settings.isCollapseNamedCharacters());
  }
}
