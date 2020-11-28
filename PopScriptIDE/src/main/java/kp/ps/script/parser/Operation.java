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
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Operation)
        {
            Operation op = (Operation) o;
            return operator.equals(op.operator) &&
                    first.equals(op.first) &&
                    Objects.equals(second, op.second) &&
                    Objects.equals(third, op.third);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.operator);
        hash = 97 * hash + Objects.hashCode(this.first);
        hash = 97 * hash + Objects.hashCode(this.second);
        hash = 97 * hash + Objects.hashCode(this.third);
        return hash;
    }
    
    
    public static final Statement unary(Operator operator, Statement operand)
    {
        if(!operator.isUnary())
            throw new IllegalArgumentException();
        
        if(operand.isLiteral() && operator.getOperatorId() == OperatorId.LOGICAL_NOT)
            return ((Literal) operand).operatorNot();
        
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
        
        Statement constResult = constantBinaryOperation(operator.getOperatorId(), left, right);
        if(constResult != null)
            return constResult;
        
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
    
    
    
    private static Statement constantBinaryOperation(OperatorId op, Statement sLeft, Statement sRight)
    {
        Literal left, right;
        if(!sLeft.isLiteral() || !sRight.isLiteral())
            return null;
        
        left = (Literal) sLeft;
        right = (Literal) sRight;
        
        switch(op)
        {
            case ADD: return left.operatorAdd(right);
            case SUBTRACT: return left.operatorSubtract(right);
            case MULTIPPLY: return left.operatorMultiply(right);
            case DIVIDE: return left.operatorDivide(right);
            case EQUALS: return left.operatorEquals(right);
            case DIFFERENT: return left.operatorNotEquals(right);
            case GREATER: return left.operatorGreater(right);
            case LESS: return left.operatorLess(right);
            case GREATER_EQUALS: return left.operatorGreaterOrEquals(right);
            case LESS_EQUALS: return left.operatorLessOrEquals(right);
        }
        
        return null;
    }
}
