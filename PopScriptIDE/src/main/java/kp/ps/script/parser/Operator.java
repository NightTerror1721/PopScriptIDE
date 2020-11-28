/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author Marc
 */
public final class Operator extends Fragment implements Comparable<Operator>
{
    private final OperatorId id;
    
    private Operator(OperatorId id) { this.id = id; }
    
    public final OperatorId getOperatorId() { return id; }
    
    public final boolean isSuffixUnary() { return id.isSuffixUnary(); }
    public final boolean isPrefixUnary() { return id.isPrefixUnary(); }
    public final boolean isUnary() { return id.isUnary(); }
    public final boolean isBinary() { return id.isBinary(); }
    public final boolean isTernary() { return id.isTernary(); }
    public final boolean isAssignment() { return id.isAssignment(); }
    public final boolean isFunctionCall() { return id.isFunctionCall(); }
    public final boolean isNamespaceResolution() { return id.isNamespaceResolution(); }
    
    public final boolean hasPriorityOver(Operator other) { return id.hasPriorityOver(other.id); }
    
    public final int comparePriorityTo(Operator other) { return id.comparePriorityTo(other.id); }
    
    public final boolean hasLeftToRightOrder() { return id.hasLeftToRightOrder(); }
    public final boolean hasRightToLeftOrder() { return id.hasRightToLeftOrder(); }
    
    public final boolean equals(Operator other)
    {
        return id == other.id;
    }
    
    @Override
    public FragmentType getFragmentType() { return FragmentType.OPERATOR; }

    @Override
    public boolean isStatement() { return false; }

    @Override
    public String toString() { return id.toString(); }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Operator)
            return equals((Operator) o);
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.id);
        return hash;
    }
    
    @Override
    public final int compareTo(Operator o) { return id.comparePriorityTo(o.id); }
    
    
    private static final HashMap<OperatorId, Operator> BY_ID = new HashMap<>();
    private static final HashMap<String, Operator> SUFFIX_UNARY = new HashMap<>();
    private static final HashMap<String, Operator> PREFIX_UNARY = new HashMap<>();
    private static final HashMap<String, Operator> BINARY = new HashMap<>();
    private static final HashMap<String, Operator> TERNARY = new HashMap<>();
    static
    {
        for(OperatorId id : OperatorId.values())
        {
            Operator op = new Operator(id);
            BY_ID.put(id, op);
            if(id.isSuffixUnary())
                SUFFIX_UNARY.put(id.getSymbol(), op);
            else if(id.isPrefixUnary())
                PREFIX_UNARY.put(id.getSymbol(), op);
            else if(id.isBinary())
                BINARY.put(id.getSymbol(), op);
            else if(id.isTernary())
                TERNARY.put(id.getSymbol(), op);
        }
    }
    
    public static final Operator fromId(OperatorId id) { return BY_ID.getOrDefault(id, null); }
    public static final Operator suffixUnaryFromSymbol(String symbol) { return SUFFIX_UNARY.getOrDefault(symbol, null); }
    public static final Operator preixUnaryFromSymbol(String symbol) { return PREFIX_UNARY.getOrDefault(symbol, null); }
    public static final Operator binaryFromSymbol(String symbol) { return BINARY.getOrDefault(symbol, null); }
    public static final Operator ternaryFromSymbol(String symbol) { return TERNARY.getOrDefault(symbol, null); }
    
    
    public static final boolean isOperator(Fragment frag, OperatorId id)
    {
        return frag instanceof Operator && ((Operator) frag).getOperatorId() == id;
    }
}
