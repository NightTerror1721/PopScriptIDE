/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.decompiler;

import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public class DecompilerException extends Exception
{
    public DecompilerException(String message) { super(message); }
    
    public DecompilerException(String message, Object... args)
    {
        super(String.format(message, args));
    }
    
    public final void printError(DecompilerState state, int identation)
    {
        state.append(Utils.stringDup(' ', identation))
                .append("/* ")
                .append(getMessage())
                .append(" */")
                .println();
    }
}
