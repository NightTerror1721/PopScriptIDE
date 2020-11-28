/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Objects;

/**
 *
 * @author mpasc
 */
public class StringFragment extends Fragment
{
    private final String string;
    
    public StringFragment(String string)
    {
        this.string = Objects.requireNonNull(string);
    }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.STRING; }

    @Override
    public final boolean isStatement() { return false; }

    @Override
    public final String toString() { return string; }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof StringFragment)
            return string.equals(((StringFragment) o).string);
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.string);
        return hash;
    }
    
}
