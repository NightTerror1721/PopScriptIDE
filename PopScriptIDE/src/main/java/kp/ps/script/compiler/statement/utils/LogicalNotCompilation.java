/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement.utils;

import java.util.Objects;
import kp.ps.script.ScriptToken;
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
public class LogicalNotCompilation implements StatementTask
{
    private final StatementTask operand;
    
    LogicalNotCompilation(StatementTask operand)
    {
        this.operand = Objects.requireNonNull(operand);
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        if(retloc.isInvalid())
            throw new CompilerException("The ! result must be stored in any valid location.");
        
        try(TemporaryVars temps = TemporaryVars.open(state, code))
        {
            MemoryAddress loc = temps.normalCompileWithTemp(operand);
            if(loc.isConstant())
                return MemoryAddress.of(loc.getConstantValue() == 0 ? 1 : 0);
            
            code.insertTokenCode(ScriptToken.IF);
            code.insertTokenCode(ScriptToken.EQUAL_TO);
            loc.compileRead(state, code);
            MemoryAddress.of(Int32.ZERO).compileRead(state, code);
            code.insertTokenCode(ScriptToken.BEGIN);
            StatementTaskUtils.assignation(retloc, StatementValue.of(Int32.ONE)).normalCompile(state, code);
            code.insertTokenCode(ScriptToken.END);
            code.insertTokenCode(ScriptToken.ELSE);
            code.insertTokenCode(ScriptToken.BEGIN);
            StatementTaskUtils.assignation(retloc, StatementValue.of(Int32.ZERO)).normalCompile(state, code);
            code.insertTokenCode(ScriptToken.END);
            
            return retloc;
        }
    }
    
    @Override
    public final MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use ! operator in var assignment.");
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        StatementValue value = operand.constCompile();
        if(!value.isConstant())
            throw new IllegalStateException();
        return StatementValue.of(value.getConstantValue().toInt() == 0 ? Int32.ONE : Int32.ZERO);
    }
    
    @Override
    public final StatementValue internalCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use ! operator in internal environment.");
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException
    {
        try(TemporaryVars temps = TemporaryVars.open(state, prev))
        {
            MemoryAddress loc = temps.normalCompileWithTemp(operand);
            if(loc.isConstant())
                return ConditionalState.evaluate(loc.getConstantValue()).inverse();
            
            cond.insertTokenCode(ScriptToken.EQUAL_TO);
            loc.compileRead(state, cond);
            MemoryAddress.of(Int32.ZERO).compileRead(state, cond);
            return ConditionalState.UNKNOWN;
        }
    }
    
}
