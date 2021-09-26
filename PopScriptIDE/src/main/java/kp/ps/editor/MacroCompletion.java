/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.editor;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;

/**
 *
 * @author Marc
 */
public class MacroCompletion extends FunctionCompletion
{
    public MacroCompletion(CompletionProvider provider, String name, String returnType)
    {
	super(provider, name, returnType);
    }
}
