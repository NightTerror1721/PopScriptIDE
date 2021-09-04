/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement;

import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public interface StatementTask
{
    MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException;
    StatementValue constCompile() throws CompilerException;
    StatementValue internalCompile() throws CompilerException;
    ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException;
    
    default MemoryAddress normalCompile(CompilerState state, CodeManager code)  throws CompilerException
    {
        return normalCompile(state, code, MemoryAddress.invalid());
    }
    
    static enum ConditionalState
    {
        UNKNOWN,
        TRUE,
        FALSE;
        
        public static final ConditionalState evaluate(int value) { return value != 0 ? TRUE : FALSE; }
        public static final ConditionalState evaluate(Int32 value) { return value.toInt() != 0 ? TRUE : FALSE; }
        
        public final ConditionalState inverse()
        {
            if(this == UNKNOWN)
                throw new IllegalStateException();
            
            return this == TRUE ? FALSE : TRUE;
        }
        
        public static final ConditionalState and(ConditionalState left, ConditionalState right)
        {
            return left == TRUE && right == TRUE ? TRUE : FALSE;
        }
        
        public static final ConditionalState or(ConditionalState left, ConditionalState right)
        {
            return left == TRUE || right == TRUE ? TRUE : FALSE;
        }
    }
}
