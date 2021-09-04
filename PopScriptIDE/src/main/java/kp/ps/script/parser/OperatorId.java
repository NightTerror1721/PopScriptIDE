/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

/**
 *
 * @author Marc
 */
public enum OperatorId
{
    NAMESPACE_RESOLUTION(".", 0, true),
    
    FUNCTION_CALL("()", 1, true),
    
    SUFFIX_INCREASE("++", 2, true),
    SUFFIX_DECREASE("--", 2, true),
    
    PREFIX_INCREASE("++", 3, false),
    PREFIX_DECREASE("--", 3, false),
    LOGICAL_NOT("!", 3, false),
    UNARY_MINUS("-", 3, false),
    
    MULTIPPLY("*", 4, true),
    DIVIDE("/", 4, true),
    
    ADD("+", 5, true),
    SUBTRACT("-", 5, true),
    
    GREATER(">", 6, true),
    LESS("<", 6, true),
    GREATER_EQUALS(">=", 6, true),
    LESS_EQUALS("<=", 6, true),
    
    EQUALS("==", 7, true),
    DIFFERENT("!=", 7, true),
    
    AND("&&", 8, true),
    
    OR("||", 9, true),
    
    ELVIS("?:", 10, true),
    
    ASSIGNATION("=", 11, false),
    ASSIGNATION_ADD("+=", 11, false),
    ASSIGNATION_SUBTRACT("-=", 11, false),
    ASSIGNATION_MULTIPLY("*=", 11, false),
    ASSIGNATION_DIVIDE("/=", 11, false);
    
    private final String symbol;
    private final int priority;
    private final boolean leftToRightOrder;
    
    private OperatorId(String symbol, int priority, boolean leftToRightOrder)
    {
        this.symbol = symbol;
        this.priority = priority;
        this.leftToRightOrder = leftToRightOrder;
    }
    
    public final String getSymbol() { return symbol; }
    public final int getPriority() { return priority; }
    public final boolean hasLeftToRightOrder() { return leftToRightOrder; }
    public final boolean hasRightToLeftOrder() { return !leftToRightOrder; }
    
    public final int comparePriorityTo(OperatorId other)
    {
        if(priority == other.priority)
            return hasRightToLeftOrder() || other.hasRightToLeftOrder() ? -1 : 0;
        return priority < other.priority ? 1 : -1;
    }
    
    public final boolean hasPriorityOver(OperatorId other)
    {
        return comparePriorityTo(other) > 0;
    }
    
    public final boolean isUnary()
    {
        switch(this)
        {
            case SUFFIX_INCREASE:
            case SUFFIX_DECREASE:
            case PREFIX_INCREASE:
            case PREFIX_DECREASE:
            case LOGICAL_NOT:
            case UNARY_MINUS:
                return true;
        }
        return false;
    }
    
    public final boolean isSuffixUnary() { return this == SUFFIX_INCREASE || this == SUFFIX_DECREASE; }
    
    public final boolean isPrefixUnary()
    {
        switch(this)
        {
            case PREFIX_INCREASE:
            case PREFIX_DECREASE:
            case LOGICAL_NOT:
            case UNARY_MINUS:
                return true;
        }
        return false;
    }
    
    public final boolean isBinary()
    {
        switch(this)
        {
            case MULTIPPLY:
            case DIVIDE:
            case ADD:
            case SUBTRACT:
            case GREATER:
            case LESS:
            case GREATER_EQUALS:
            case LESS_EQUALS:
            case EQUALS:
            case DIFFERENT:
            case AND:
            case OR:
            case ASSIGNATION:
            case ASSIGNATION_ADD:
            case ASSIGNATION_SUBTRACT:
            case ASSIGNATION_MULTIPLY:
            case ASSIGNATION_DIVIDE:
                return true;
        }
        return false;
    }
    
    public final boolean isTernary()
    {
        return this == ELVIS;
    }
    
    public final boolean isAssignment()
    {
        switch(this)
        {
            case ASSIGNATION:
            case ASSIGNATION_ADD:
            case ASSIGNATION_SUBTRACT:
            case ASSIGNATION_MULTIPLY:
            case ASSIGNATION_DIVIDE:
                return true;
        }
        return false;
    }
    
    public final boolean isFunctionCall() { return this == FUNCTION_CALL; }
    
    public final boolean isNamespaceResolution() { return this == NAMESPACE_RESOLUTION; }
}
