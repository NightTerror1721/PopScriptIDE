/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;

/**
 *
 * @author mpasc
 */
public abstract class Instruction
{
    private int firstLine;
    private int lastLine;
    
    Instruction(int firstLine, int lastLine)
    {
        this.firstLine = Math.min(firstLine, lastLine);
        this.lastLine = Math.max(lastLine, firstLine);
    }
    
    abstract void normalCompile(CompilerState state, CodeManager code) throws CompilerException;
    abstract void constCompile(CompilerState state) throws CompilerException;
    abstract void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException;
    
    final void updateLastLine(int line)
    {
        int first = firstLine;
        this.firstLine = Math.min(first, line);
        this.lastLine = Math.max(line, first);
    }
    
    public final int getFirstLine() { return firstLine; }
    public final int getLastLine() { return lastLine; }
    
    public abstract boolean hasYieldInstruction();
    
    public boolean isYieldInstruction() { return false; }
    
    public boolean isStrictInstruction() { return false; }
    
    /*abstract String toString(int currentIdent, int deltaIdent);
    
    public final String toString(int identation) { return toString(0, Math.max(0, identation)); }
    
    @Override
    public final String toString() { return toString(4); }*/
}
