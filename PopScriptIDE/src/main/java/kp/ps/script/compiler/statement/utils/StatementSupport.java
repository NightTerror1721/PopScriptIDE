/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement.utils;

import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementTask.ConditionalState;

/**
 *
 * @author Marc
 */
public final class StatementSupport
{
    private StatementSupport() {}
    
    public static final StatementTask assignation(StatementTask dst, StatementTask src)
    {
        return new AssignmentCompilation(dst, src);
    }
    
    public static final StatementTask sum(StatementTask left, StatementTask right)
    {
        return new SumSubCompilation(left, right, true);
    }
    
    public static final StatementTask sub(StatementTask left, StatementTask right)
    {
        return new SumSubCompilation(left, right, false);
    }
    
    public static final StatementTask mul(StatementTask left, StatementTask right)
    {
        return new MulDivCompilation(left, right, true);
    }
    
    public static final StatementTask div(StatementTask left, StatementTask right)
    {
        return new MulDivCompilation(left, right, false);
    }
    
    public static final StatementTask inc(StatementTask operand, boolean isPrefix)
    {
        return new IncDecCompilation(operand, isPrefix, true);
    }
    
    public static final StatementTask dec(StatementTask operand, boolean isPrefix)
    {
        return new IncDecCompilation(operand, isPrefix, false);
    }
    
    public static final StatementTask not(StatementTask operand)
    {
        return new LogicalNotCompilation(operand);
    }
    
    public static final StatementTask equals(StatementTask left, StatementTask right)
    {
        return new ConditionalOperatorCompilation(left, right, ConditionalOperatorCompilation.Mode.EQUALS);
    }
    
    public static final StatementTask notEquals(StatementTask left, StatementTask right)
    {
        return new ConditionalOperatorCompilation(left, right, ConditionalOperatorCompilation.Mode.NOT_EQUALS);
    }
    
    public static final StatementTask greater(StatementTask left, StatementTask right)
    {
        return new ConditionalOperatorCompilation(left, right, ConditionalOperatorCompilation.Mode.GREATER);
    }
    
    public static final StatementTask greaterOrEquals(StatementTask left, StatementTask right)
    {
        return new ConditionalOperatorCompilation(left, right, ConditionalOperatorCompilation.Mode.GREATER_EQUALS);
    }
    
    public static final StatementTask less(StatementTask left, StatementTask right)
    {
        return new ConditionalOperatorCompilation(left, right, ConditionalOperatorCompilation.Mode.LESS);
    }
    
    public static final StatementTask lessOrEquals(StatementTask left, StatementTask right)
    {
        return new ConditionalOperatorCompilation(left, right, ConditionalOperatorCompilation.Mode.LESS_EQUALS);
    }
    
    public static final StatementTask elvis(StatementTask condition, StatementTask ifIsTrue, StatementTask ifIsFalse)
    {
        return new ElvisOperatorCompilation(condition, ifIsTrue, ifIsFalse);
    }
    
    
    
    
    
    
    public static final ConditionalState compileIfCommand(CompilerState state, CodeManager code, StatementTask condition) throws CompilerException
    {
        CodeManager prev = new CodeManager();
        CodeManager cond = new CodeManager();
        
        ConditionalState result = condition.conditionalCompile(state, prev, cond);
        if(result != ConditionalState.UNKNOWN)
            return result;
        
        code.insertCode(prev);
        code.insertTokenCode(ScriptToken.IF);
        code.insertCode(cond);
        
        return result;
    }
}
