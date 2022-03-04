/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement.utils;

import java.util.Objects;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.functions.Macro;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;

/**
 *
 * @author Marc
 */
public class MacroCallCompilation implements StatementTask
{
    private final Macro macro;
    private final StatementTask[] args;
    
    MacroCallCompilation(Macro macro, StatementTask[] args)
    {
        this.macro = Objects.requireNonNull(macro);
        this.args = args == null ? new StatementTask[0] : args;
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        return macro.compile(state, code, args, retloc);
    }

    @Override
    public MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use call operator in var assignment.");
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use call operator in const assignment.");
    }

    @Override
    public StatementValue internalCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use call operator in internal assignment.");
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond, TemporaryVars temps) throws CompilerException
    {
        return macro.conditionalCompile(state, prev, cond, temps, args, this);
    }
}
