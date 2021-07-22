/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author Marc
 */
public final class Identifier extends Statement implements Comparable<Identifier>
{
    private final String identifier;
    
    private Identifier(String identifier)
    {
        this.identifier = Objects.requireNonNull(identifier);
    }
    
    public final String getIdentifier() { return identifier; }
    
    public final boolean equals(Identifier o)
    {
        return identifier.equals(o.identifier);
    }
    
    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*$");
    public static final boolean isValid(String str)
    {
        return PATTERN.matcher(str).matches();
    }
    
    public static final Identifier valueOf(String name) throws SyntaxException
    {
        if(!isValid(name))
            throw new SyntaxException("Invalid identifier format.");
        return new Identifier(name);
    }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.IDENTIFIER; }

    @Override
    public String toString() { return identifier; }

    @Override
    public final int compareTo(Identifier o)
    {
        return identifier.compareTo(o.identifier);
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Identifier)
            return identifier.equals(((Identifier) o).identifier);
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.identifier);
        return hash;
    }
}
