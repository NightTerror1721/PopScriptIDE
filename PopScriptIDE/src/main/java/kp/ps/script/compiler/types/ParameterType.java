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
public abstract class ParameterType
{
    public abstract TypeId getType();
    public abstract TypeModifier getModifier();
    public abstract boolean hasModifier();
    
    public final boolean isCompatible(CompleteType type)
    {
        if(getType() != type.getType())
            return false;
        
        if(hasModifier())
            return getModifier() == type.getModifier();
        return true;
    }
    
    public static final ParameterType of(TypeId type) { return new NormalParameterType(type); }
    public static final ParameterType of(CompleteType type) { return new ModifiedParameterType(type); }
    
    public static final ParameterType of(TypeModifier modifier, TypeId type) throws CompilerException
    {
        if(modifier == null)
            return of(type);
        return of(type.complete(modifier));
    }
    
    @Override
    public final String toString()
    {
        if(!hasModifier())
            return getType().getTypeName();
        switch(getModifier())
        {
            case VAR: return getModifier().getModifierName();
            case CONST: return getModifier().getModifierName() + " " + getType().getTypeName();
            case INTERNAL: return getType() == TypeId.INT
                    ? getModifier().getModifierName()
                    : getType().getTypeName();
        }
        return getModifier().getModifierName() + " " + getType().getTypeName();
    }
    
    
    
    private static final class NormalParameterType extends ParameterType
    {
        private final TypeId type;
        
        private NormalParameterType(TypeId type)
        {
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public final TypeId getType() { return type; }

        @Override
        public final TypeModifier getModifier() { throw new IllegalStateException(); }

        @Override
        public final boolean hasModifier() { return false; }
    }
    
    private static final class ModifiedParameterType extends ParameterType
    {
        private final CompleteType type;
        
        private ModifiedParameterType(CompleteType type)
        {
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public final TypeId getType() { return type.getType(); }

        @Override
        public final TypeModifier getModifier() { return type.getModifier(); }

        @Override
        public final boolean hasModifier() { return true; }
    }
}
