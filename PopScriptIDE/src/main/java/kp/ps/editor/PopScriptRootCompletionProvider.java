/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import java.util.List;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

/**
 *
 * @author Marc
 */
public class PopScriptRootCompletionProvider extends DefaultCompletionProvider
{
    final List<Completion> getCompletions() { return completions; }
}
