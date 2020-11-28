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
public class Separator extends Fragment
{
    private final char symbol;
    
    private Separator(char symbol) { this.symbol = symbol; }
    
    public final boolean equals(Separator other) { return symbol == other.symbol; }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.SEPARATOR; }

    @Override
    public final boolean isStatement() { return false; }

    @Override
    public final String toString() { return Character.toString(symbol); }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Separator)
            return symbol == ((Separator) o).symbol;
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 13 * hash + this.symbol;
        return hash;
    }
    
    
    
    public static final Separator COMMA = new Separator(',');
    public static final Separator SEMI_COLON = new Separator(';');
    public static final Separator TWO_POINTS = new Separator(':');
    public static final Separator BEGIN_PARENTHESIS = new Separator('(');
    public static final Separator END_PARENTHESIS = new Separator(')');
}
