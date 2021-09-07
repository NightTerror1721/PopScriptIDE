/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.util.Objects;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementCompiler;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.parser.Fragment;
import kp.ps.script.parser.Statement;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class EveryInstruction extends Instruction
{
    private final Statement period;
    private final Statement delay;
    private final Fragment block;
    
    private EveryInstruction(Statement period, Statement delay, Fragment block)
    {
        this.period = Objects.requireNonNull(period);
        this.delay = delay;
        this.block = Objects.requireNonNull(block);
    }
    
    @Override
    public void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        StatementTask periodTask = StatementCompiler.toTask(state, period);
        StatementTask delayTask = delay != null ? StatementCompiler.toTask(state, period) : null;
        
        code.insertTokenCode(ScriptToken.EVERY);
        Int32 iPeriod = periodTask.constCompile().getConstantValue();
        switch(iPeriod.toInt())
        {
            case 2:
            case 4:
            case 8:
            case 16:
            case 32:
            case 64:
            case 128:
            case 256:
            case 512:
            case 1024:
            case 2048:
            case 4096:
            case 8192:
                MemoryAddress.of(iPeriod).compileRead(state, code);
                break;
                
            default:
                throw new CompilerException("Expected valid const int from "
                        + "[2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192] "
                        + "in 'every' period arg. But found '%s'.", iPeriod);
        }
        
        if(delayTask != null)
        {
            Int32 iDelay = delayTask.constCompile().getConstantValue();
            if(iDelay.toInt() < 1 || iDelay.toInt() >= iPeriod.toInt())
                throw new CompilerException("Invalid delay arg of 'every' command. Excepted value of range [%s, %s] but found %s.",
                        1, iPeriod.toInt() - 1, iDelay);
            
            MemoryAddress.of(iDelay).compileRead(state, code);
        }
    }

    @Override
    public void constCompile(CompilerState state) throws CompilerException
    {
        throw new CompilerException("Cannot use 'every' command in constant environment.");
    }
    
}
