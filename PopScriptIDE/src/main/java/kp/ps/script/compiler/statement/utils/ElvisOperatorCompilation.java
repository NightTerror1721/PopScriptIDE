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
import kp.ps.script.compiler.statement.StatementCompiler;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class ElvisOperatorCompilation implements StatementTask
{
    private final StatementTask conditionOperand;
    private final StatementTask trueOperand;
    private final StatementTask falseOperand;
    
    ElvisOperatorCompilation(StatementTask conditionOperand, StatementTask trueOperand, StatementTask falseOperand)
    {
        this.conditionOperand = Objects.requireNonNull(conditionOperand);
        this.trueOperand = Objects.requireNonNull(trueOperand);
        this.falseOperand = Objects.requireNonNull(falseOperand);
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        if(retloc.isInvalid())
            throw new CompilerException("Result of ?: operator must be stored in valid location (varriale or internal).");
        
        try(TemporaryVars temps = TemporaryVars.open(state, code))
        {
            switch(StatementCompiler.compileIfCommand(state, code, conditionOperand))
            {
                case TRUE: {
                    MemoryAddress alloc = temps.normalCompileWithTemp(trueOperand);
                    StatementTaskUtils.assignation(retloc, alloc).normalCompile(state, code);
                } break;
                    
                case FALSE: {
                    MemoryAddress alloc = temps.normalCompileWithTemp(falseOperand);
                    StatementTaskUtils.assignation(retloc, alloc).normalCompile(state, code);
                } break;
                    
                case UNKNOWN: {
                    MemoryAddress alloc;
                    code.insertTokenCode(ScriptToken.BEGIN);
                    alloc = temps.normalCompileWithTemp(trueOperand);
                    StatementTaskUtils.assignation(retloc, alloc).normalCompile(state, code);
                    code.insertTokenCode(ScriptToken.END);
                    code.insertTokenCode(ScriptToken.ELSE);
                    code.insertTokenCode(ScriptToken.BEGIN);
                    alloc = temps.normalCompileWithTemp(falseOperand);
                    StatementTaskUtils.assignation(retloc, alloc).normalCompile(state, code);
                    code.insertTokenCode(ScriptToken.END);
                } break;
                
                default:
                    throw new IllegalStateException();
            }
            
            return retloc;
        }
    }
    
    @Override
    public final MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use ?: operator in var assignment.");
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        StatementValue cond = conditionOperand.constCompile();
        StatementValue istrue = trueOperand.constCompile();
        StatementValue isfalse = falseOperand.constCompile();
        
        if(!cond.isConstant() || !istrue.isConstant() || !isfalse.isConstant())
            throw new IllegalStateException();
        
        Int32 result = cond.getConstantValue().toInt() != 0 ? istrue.getConstantValue() : isfalse.getConstantValue();
        return StatementValue.of(result);
    }
    
    @Override
    public final StatementValue internalCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use ?: operator in internal environment.");
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException
    {
        try(TemporaryVars temps = TemporaryVars.open(state, prev))
        {
            return temps.normalCompileWithTemp(this).conditionalCompile(state, prev, cond);
        }
    }
    
}
