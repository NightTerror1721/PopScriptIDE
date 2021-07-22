/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

/**
 *
 * @author Marc
 */
public class CompilerException extends Exception
{
    public CompilerException(String message) { super(message); }
    public CompilerException(String messageFormat, Object... values)
    {
        super(String.format(messageFormat, values));
    }
}
