/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Objects;

/**
 *
 * @author Marc
 */
public final class Operation extends Statement
{
    private final Operator operator;
    private final Statement first, second, third;
    
    private Operation(Operator operator, Statement first, Statement second, Statement third)
    {
        this.operator = Objects.requireNonNull(operator);
        this.first = Objects.requireNonNull(first);
        this.second = second;
        this.third = third;
        
        if(operator.isUnary())
        {
            if(second != null || third != null)
                throw new IllegalArgumentException();
        }
        else if(operator.isBinary())
        {
            if(second == null || third != null)
                throw new IllegalArgumentException();
        }
        else if(operator.isTernary())
        {
            if(second == null || third == null)
                throw new IllegalArgumentException();
        }
        else if(operator.isFunctionCall())
        {
            if(second == null || third != null)
                throw new IllegalArgumentException();
        }
    }
    
    public final boolean isSuffixUnary() { return operator.isSuffixUnary(); }
    public final boolean isPrefixUnary() { return operator.isPrefixUnary(); }
    public final boolean isBinary() { return operator.isBinary(); }
    public final boolean isTernary() { return operator.isTernary(); }
    
    public final Statement getUnaryOperand() { return first; }
    
    public final Statement getBinaryLeftOperand() { return first; }
    public final Statement getBinaryRightOperand() { return second; }
    
    
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.OPERATION; }

    @Override
    public final String toString()
    {
        if(isSuffixUnary())
            return "(" + first + ")" + operator;
        if(isPrefixUnary())
            return operator + "(" + first + ")";
        if(isBinary())
            return "(" + first + " " + operator + " " + second + ")";
        return "(" + first + " ? " + second + " : " + third + ")";
    }
    
    
    public static final Statement unary(Operator operator, Statement operand)
    {
        if(!operator.isUnary())
            throw new IllegalArgumentException();
        return new Operation(operator, operand, null, null);
    }
    
    public static final Statement binary(Operator operator, Statement left, Statement right) throws SyntaxException
    {
        if(!operator.isBinary())
            throw new IllegalArgumentException();
        
        switch(operator.getOperatorId())
        {
            case ASSIGNATION:
            case ASSIGNATION_ADD:
            case ASSIGNATION_SUBTRACT:
            case ASSIGNATION_MULTIPLY:
            case ASSIGNATION_DIVIDE:
                return assignment(operator, left, right);
        }
        
        return new Operation(operator, left, right, null);
    }
    
    public static final Statement elvis(Statement condition, Statement ifIsTrue, Statement ifIsFalse)
    {
        return new Operation(Operator.fromId(OperatorId.ELVIS), condition, ifIsTrue, ifIsFalse);
    }
    
    public static final Statement assignment(Operator operator, Statement left, Statement right) throws SyntaxException
    {
        return new Assignment(operator, left, right);
    }
    
    public static final Statement functionCall(Statement identifier, Fragment args) throws SyntaxException
    {
        return new FunctionCall(identifier, args);
    }
    
    public static final Statement namespaceResolver(Statement base, Fragment identifier) throws SyntaxException
    {
        return new NamespaceResolver(base, identifier);
    }
}
