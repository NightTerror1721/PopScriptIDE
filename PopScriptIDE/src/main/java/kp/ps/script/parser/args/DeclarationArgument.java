/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser.args;

import java.util.Objects;
import kp.ps.script.parser.ElementReference;
import kp.ps.script.parser.Identifier;
import kp.ps.script.parser.Type;

/**
 *
 * @author Marc
 */
final class DeclarationArgument extends Argument
{
    private final Type type;
    private final Identifier identifier;
    private final ElementReference defaultValue;
    
    DeclarationArgument(Type type, Identifier identifier, ElementReference defaultValue)
    {
        this.type = Objects.requireNonNull(type);
        this.identifier = Objects.requireNonNull(identifier);
        this.defaultValue = defaultValue;
    }
    
    @Override
    public final boolean isDeclarationArgument() { return true; }
    
    @Override
    public final Type getDeclarationType() { return type; }
    
    @Override
    public final Identifier getDeclarationIdentifier() { return identifier; }
    
    @Override
    public final ElementReference getDeclarationDefaultValue() { return defaultValue; }
    
    @Override
    public final String toString() { return type + " " + identifier; }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof DeclarationArgument)
        {
            DeclarationArgument da = (DeclarationArgument) o;
            return type.equals(da.type) && identifier.equals(da.identifier);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.identifier);
        return hash;
    }
}
