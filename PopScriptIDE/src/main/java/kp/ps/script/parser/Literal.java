/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Objects;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class Literal extends Statement
{
    public static final Literal ONE = new Literal(Int32.valueOf(1));
    public static final Literal ZERO = new Literal(Int32.valueOf(0));
    public static final Literal MINUSONE = new Literal(Int32.valueOf(-1));
    
    private final Int32 value;
    
    public Literal(Int32 value)
    {
        this.value = Objects.requireNonNull(value);
    }
    
    public final Int32 getValue() { return value; }

    @Override
    public final FragmentType getFragmentType() { return FragmentType.LITERAL; }

    @Override
    public String toString() { return value.toString(); }
    
    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Literal)
            return value.equals(((Literal) o).value);
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.value);
        return hash;
    }
    
    public static final Literal parse(String text)
    {
        try { return new Literal(Int32.valueOf(Integer.decode(text))); }
        catch(NumberFormatException ex) { return null; }
    }
    
    public static final boolean isValid(String text) { return parse(text) != null; }
    
    
    public final Literal operatorAdd(Literal right)
    {
        return new Literal(Int32.valueOf(value.toInt() + right.value.toInt()));
    }
    
    public final Literal operatorSubtract(Literal right)
    {
        return new Literal(Int32.valueOf(value.toInt() - right.value.toInt()));
    }
    
    public final Literal operatorMultiply(Literal right)
    {
        return new Literal(Int32.valueOf(value.toInt() * right.value.toInt()));
    }
    
    public final Literal operatorDivide(Literal right)
    {
        return new Literal(Int32.valueOf(value.toInt() / right.value.toInt()));
    }
    
    public final Literal operatorNegative()
    {
        return new Literal(Int32.valueOf(-value.toInt()));
    }
    
    public final Literal operatorTest()
    {
        return new Literal(value.toInt() != 0 ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorNot()
    {
        return new Literal(value.toInt() == 0 ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorEquals(Literal right)
    {
        return new Literal(value.toInt() == right.value.toInt() ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorNotEquals(Literal right)
    {
        return new Literal(value.toInt() != right.value.toInt() ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorGreater(Literal right)
    {
        return new Literal(value.toInt() > right.value.toInt() ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorLess(Literal right)
    {
        return new Literal(value.toInt() < right.value.toInt() ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorGreaterOrEquals(Literal right)
    {
        return new Literal(value.toInt() >= right.value.toInt() ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorLessOrEquals(Literal right)
    {
        return new Literal(value.toInt() <= right.value.toInt() ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorAnd(Literal right)
    {
        return new Literal(value.toInt() != 0 && right.value.toInt() != 0 ? Int32.ONE : Int32.ZERO);
    }
    
    public final Literal operatorOr(Literal right)
    {
       return new Literal(value.toInt() != 0 || right.value.toInt() != 0 ? Int32.ONE : Int32.ZERO);
    }
}
