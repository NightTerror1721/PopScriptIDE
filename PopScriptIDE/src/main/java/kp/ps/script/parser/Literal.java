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
    private Int32 value;
    
    public Literal(Int32 value)
    {
        this.value = Objects.requireNonNull(value);
    }
    
    public final Int32 getValue() { return value; }

    @Override
    public final FragmentType getFragmentType() { return FragmentType.LITERAL; }

    @Override
    public String toString() { return value.toString(); }
    
    public static final Literal parse(String text)
    {
        try { return new Literal(Int32.valueOf(Integer.decode(text))); }
        catch(NumberFormatException ex) { return null; }
    }
    
    public static final boolean isValid(String text) { return parse(text) != null; }
}
