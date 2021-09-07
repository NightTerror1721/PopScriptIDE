/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.types;

import java.util.Objects;
import kp.ps.script.compiler.CompilerException;

/**
 *
 * @author Marc
 */
public class CompleteType
{
    private final TypeId type;
    private final TypeModifier modifier;
    
    CompleteType(TypeModifier modifier, TypeId type) throws CompilerException
    {
        this.type = type == null ? TypeId.INT : type;
        if(modifier == null)
            this.modifier = this.type == TypeId.INT ? TypeModifier.VAR : TypeModifier.INTERNAL;
        else this.modifier = modifier;
        
        switch(this.modifier)
        {
            case VAR:
                if(this.type != TypeId.INT)
                    throw new CompilerException("Only int type can take var modifier.");
                break;
                
            case CONST:
                if(this.type != TypeId.INT)
                    throw new CompilerException("Only int type can take const modifier.");
                break;
        }
    }
    
    public final TypeId getType() { return type; }
    public final TypeModifier getModifier() { return modifier; }
    
    public final boolean equals(CompleteType other)
    {
        return type == other.type && modifier == other.modifier;
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        
        if(o == null)
            return false;
        
        if(o instanceof CompleteType)
        {
            CompleteType ct = (CompleteType) o;
            return type == ct.type && modifier == ct.modifier;
        }
        
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.modifier);
        return hash;
    }
}
