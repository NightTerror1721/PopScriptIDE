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
public class ConditionalOperatorCompilation implements StatementTask
{
    private final StatementTask leftOperand;
    private final StatementTask rightOperand;
    private final Mode mode;
    
    ConditionalOperatorCompilation(StatementTask leftOperand, StatementTask rightOperand, Mode mode)
    {
        this.leftOperand = Objects.requireNonNull(leftOperand);
        this.rightOperand = Objects.requireNonNull(rightOperand);
        this.mode = Objects.requireNonNull(mode);
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        if(retloc.isInvalid())
            throw new CompilerException("Operator %s must store result in any valid location.", mode.operatorName);
        
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
            code.insertTokenCode(ScriptToken.ENDIF);
        }

        return retloc;
    }
    
    @Override
    public final MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use %s operator in var assignment.", mode.operatorName);
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        StatementValue left = leftOperand.constCompile();
        StatementValue right = rightOperand.constCompile();
        
        if(!left.isConstant() || !right.isConstant())
            throw new IllegalStateException();
        
        return StatementValue.of(constOperation(left.getConstantValue().toInt(), right.getConstantValue().toInt()));
    }
    
    @Override
    public final StatementValue internalCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use %s operator in internal environment.", mode.operatorName);
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond, TemporaryVars temps) throws CompilerException
    {
        MemoryAddress left = temps.normalCompileWithTemp(leftOperand);
        MemoryAddress right = temps.normalCompileWithTemp(rightOperand);

        if(left.isConstant() && right.isConstant())
            return ConditionalState.evaluate(constOperation(left.getConstantValue(), right.getConstantValue()));

        cond.insertTokenCode(mode.token);
        left.compileRead(state, cond);
        right.compileRead(state, cond);

        return ConditionalState.UNKNOWN;
    }
    
    private int constOperation(int left, int right)
    {
        switch(mode)
        {
            case EQUALS: return left == right ? 1 : 0;
            case NOT_EQUALS: return left != right ? 1 : 0;
            case GREATER: return left > right ? 1 : 0;
            case GREATER_EQUALS: return left >= right ? 1 : 0;
            case LESS: return left < right ? 1 : 0;
            case LESS_EQUALS: return left <= right ? 1 : 0;
        }
        throw new IllegalStateException();
    }
    
    static enum Mode
    {
        EQUALS(ScriptToken.EQUAL_TO, "=="),
        NOT_EQUALS(ScriptToken.NOT_EQUAL_TO, "!="),
        GREATER(ScriptToken.GREATER_THAN, ">"),
        GREATER_EQUALS(ScriptToken.GREATER_THAN_EQUAL_TO, ">="),
        LESS(ScriptToken.LESS_THAN, "<"),
        LESS_EQUALS(ScriptToken.LESS_THAN_EQUAL_TO, "<=");
        
        private final ScriptToken token;
        private final String operatorName;
        
        private Mode(ScriptToken token, String operatorName)
        {
            this.token = token;
            this.operatorName = operatorName;
        }
    }
}
