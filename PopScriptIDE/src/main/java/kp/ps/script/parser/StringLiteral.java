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
public class StringLiteral extends Fragment
{
    private final String string;
    
    public StringLiteral(String string)
    {
        this.string = Objects.requireNonNull(string);
    }
    
    public final String getString() { return string; }
    
    @Override
    public FragmentType getFragmentType() { return FragmentType.STRING_LITERAL; }

    @Override
    public boolean isStatement() { return false; }

    @Override
    public String toString() { return '\"' + string + '\"'; }

    @Override
    public boolean equals(Object o)
    {
        if(o == this)
            return true;
        
        if(o == null)
            return false;
        
        if(o instanceof StringLiteral)
            return string.equals(((StringLiteral) o).string);
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.string);
        return hash;
    }
}
