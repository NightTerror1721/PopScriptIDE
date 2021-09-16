/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement.utils;

import kp.ps.script.compiler.functions.InnerFunction;
import kp.ps.script.compiler.functions.Macro;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public final class StatementTaskUtils
{
    private StatementTaskUtils() {}
    
    public static final StatementTask assignation(StatementTask dst, StatementTask src)
    {
        return new AssignmentCompilation(dst, src);
    }
    
    public static final StatementTask sum(StatementTask left, StatementTask right)
    {
        return new SumSubCompilation(left, right, true, false);
    }
    
    public static final StatementTask sub(StatementTask left, StatementTask right)
    {
        return new SumSubCompilation(left, right, false, false);
    }
    
    public static final StatementTask mul(StatementTask left, StatementTask right)
    {
        return new MulDivCompilation(left, right, true, false);
    }
    
    public static final StatementTask div(StatementTask left, StatementTask right)
    {
        return new MulDivCompilation(left, right, false, false);
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
    
    public static final StatementTask and(StatementTask left, StatementTask right)
    {
        return new AndOrCompilation(left, right, true);
    }
    
    public static final StatementTask or(StatementTask left, StatementTask right)
    {
        return new AndOrCompilation(left, right, false);
    }
    
    public static final StatementTask assignationSum(StatementTask left, StatementTask right)
    {
        return new SumSubCompilation(left, right, true, true);
    }
    
    public static final StatementTask assignationSub(StatementTask left, StatementTask right)
    {
        return new SumSubCompilation(left, right, false, true);
    }
    
    public static final StatementTask assignationMul(StatementTask left, StatementTask right)
    {
        return new MulDivCompilation(left, right, true, true);
    }
    
    public static final StatementTask assignationDiv(StatementTask left, StatementTask right)
    {
        return new MulDivCompilation(left, right, false, true);
    }
    
    public static final StatementTask negative(StatementTask operand)
    {
        return mul(operand, StatementValue.of(Int32.MINUSONE));
    }
    
    public static final StatementTask actionCall(InnerFunction function, StatementTask[] args)
    {
        return new ActionCallCompilation(function, args);
    }
    
    public static final StatementTask macroCall(Macro macro, StatementTask[] args)
    {
        return new MacroCallCompilation(macro, args);
    }
}
