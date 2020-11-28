/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import kp.ps.script.compiler.CompilerException;

/**
 *
 * @author Marc
 */
public class SyntaxException extends CompilerException
{
    public SyntaxException(String message)
    {
        super("syntax error: " + message);
    }
}
