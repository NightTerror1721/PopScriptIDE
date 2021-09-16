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
public class AndOrCompilation implements StatementTask
{
    private final StatementTask leftOperand;
    private final StatementTask rightOperand;
    private final boolean andMode;
    
    AndOrCompilation(StatementTask leftOperand, StatementTask rightOperand, boolean andMode)
    {
        this.leftOperand = Objects.requireNonNull(leftOperand);
        this.rightOperand = Objects.requireNonNull(rightOperand);
        this.andMode = andMode;
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        if(retloc.isInvalid())
            throw new CompilerException("Operator %s must store result in any valid location.", (andMode ? "and" : "or"));
        
        ConditionalState resultState = StatementCompiler.compileIfCommand(state, code, this);
        if(resultState != ConditionalState.UNKNOWN)
        {
            if(resultState == ConditionalState.TRUE)
                StatementTaskUtils.assignation(retloc, StatementValue.of(Int32.ONE)).normalCompile(state, code);
            else StatementTaskUtils.assignation(retloc, StatementValue.of(Int32.ZERO)).normalCompile(state, code);
        }
        else
        {
            code.insertTokenCode(ScriptToken.BEGIN);
            StatementTaskUtils.assignation(retloc, StatementValue.of(Int32.ONE)).normalCompile(state, code);
            code.insertTokenCode(ScriptToken.END);
            code.insertTokenCode(ScriptToken.ELSE);
            code.insertTokenCode(ScriptToken.BEGIN);
            StatementTaskUtils.assignation(retloc, StatementValue.of(Int32.ZERO)).normalCompile(state, code);
            code.insertTokenCode(ScriptToken.END);
        }

        return retloc;
    }
    
    @Override
    public final MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use %s operator in var assignment.", (andMode ? "&&" : "||"));
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        StatementValue left = leftOperand.constCompile();
        StatementValue right = rightOperand.constCompile();
        
        if(!left.isConstant() || !right.isConstant())
            throw new IllegalStateException();
        
        Int32 result;
        if(andMode)
            result = left.getConstantValue().toInt() != 0 && right.getConstantValue().toInt() != 0 ? Int32.ONE : Int32.ZERO;
        else result = left.getConstantValue().toInt() != 0 || right.getConstantValue().toInt() != 0 ? Int32.ONE : Int32.ZERO;
        
        return StatementValue.of(result);
    }
    
    @Override
    public final StatementValue internalCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use %s operator in internal environment.", (andMode ? "&&" : "||"));
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond, TemporaryVars temps) throws CompilerException
    {
        CodeManager leftPrev = new CodeManager();
        CodeManager rightPrev = new CodeManager();
        CodeManager leftCond = new CodeManager();
        CodeManager rightCond = new CodeManager();
        
        ConditionalState leftState = leftOperand.conditionalCompile(state, leftPrev, leftCond, temps);
        ConditionalState rightState = rightOperand.conditionalCompile(state, rightPrev, rightCond, temps);
        
        if(leftState != ConditionalState.UNKNOWN && rightState != ConditionalState.UNKNOWN)
        {
            if(andMode)
                return ConditionalState.and(leftState, rightState);
            return ConditionalState.or(leftState, rightState);
        }
        
        prev.insertCode(leftPrev);
        prev.insertCode(rightPrev);
        
        cond.insertTokenCode(andMode ? ScriptToken.AND : ScriptToken.OR);
        if(leftState != ConditionalState.UNKNOWN)
            setLiteral(state, cond, leftState == ConditionalState.TRUE);
        else cond.insertCode(leftCond);
        if(rightState != ConditionalState.UNKNOWN)
            setLiteral(state, cond, rightState == ConditionalState.TRUE);
        else cond.insertCode(rightCond);
        
        return ConditionalState.UNKNOWN;
    }
    
    private static void setLiteral(CompilerState state, CodeManager cond, boolean value) throws CompilerException
    {
        cond.insertTokenCode(ScriptToken.EQUAL_TO);
        MemoryAddress.of(Int32.ONE).compileRead(state, cond);
        MemoryAddress.of(value ? Int32.ONE : Int32.ZERO).compileRead(state, cond);
    }
}
