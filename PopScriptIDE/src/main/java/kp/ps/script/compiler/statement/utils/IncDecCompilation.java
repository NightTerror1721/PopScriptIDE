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
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class IncDecCompilation implements StatementTask
{
    private final StatementTask operand;
    private final boolean prefix;
    private final boolean incMode;
    
    IncDecCompilation(StatementTask operand, boolean prefix, boolean incMode)
    {
        this.operand = Objects.requireNonNull(operand);
        this.prefix = prefix;
        this.incMode = incMode;
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        MemoryAddress var = operand.normalCompile(state, code);
        if(!var.canWrite())
            throw new CompilerException("Required valid user or internal variable to store %s operator result", (incMode ? "increase" : "decrease"));
        
        if(prefix && !retloc.isInvalid())
            StatementTaskUtils.assignation(retloc, var);
        new SumSubCompilation(operand, StatementValue.of(Int32.ONE), incMode, false).normalCompile(state, code, var);
        if(!prefix && !retloc.isInvalid())
            StatementTaskUtils.assignation(retloc, var);
        
        return retloc.isInvalid() ? var : retloc;
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use %s operator in const environment.", (incMode ? "++" : "--"));
    }
    
    @Override
    public final StatementValue internalCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use %s operator in internal environment.", (incMode ? "++" : "--"));
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException
    {
        try(TemporaryVars temps = TemporaryVars.open(state, prev))
        {
            return normalCompile(state, prev, temps.push()).conditionalCompile(state, prev, cond);
        }
    }
    
}
