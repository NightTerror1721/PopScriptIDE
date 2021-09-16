/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.HashMap;
import java.util.Objects;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.types.ParameterType;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.compiler.types.TypeModifier;

/**
 *
 * @author Marc
 */
public final class Type extends Fragment
{
    private TypeId id;
    private TypeModifier modifier;
    
    private Type(TypeModifier modifier, TypeId type)
    {
        this.id = type;
        this.modifier = modifier;
    }
    
    public final TypeId getTypeId() { return id == null ? TypeId.INT : id; }
    
    public final TypeModifier getModifier()
    {
        if(modifier != null)
            return modifier;
        
        switch(getTypeId())
        {
            case INT: return TypeModifier.VAR;
            default: return TypeModifier.INTERNAL;
        }
    }
    
    public final ParameterType getParameterType() throws CompilerException
    {
        return ParameterType.of(modifier, getTypeId());
    }
    
    
    public final String getTypeName() { return getTypeId().getTypeName(); }
    public final String getModifierName() { return getModifier().getModifierName(); }
    
    public final void insertTypeId(TypeId type) throws CompilerException
    {
        if(id != null)
            throw new CompilerException("Invalid type: '%s'.", (toString() + " " + type.getTypeName()));
        
        if(Objects.requireNonNull(type) != TypeId.INT && getModifier() != TypeModifier.INTERNAL)
            throw new CompilerException("Invalid type modifier: '%s' cannot combine with '%s'.", type, getModifier());
        
        this.id = type;
    }
    
    public final void insertModifier(TypeModifier mod) throws CompilerException
    {
        if(modifier != null)
            throw new CompilerException("Invalid type: '%s'.", (toString() + " " + mod.getModifierName()));
        
        if(getTypeId() != TypeId.INT && Objects.requireNonNull(mod) != TypeModifier.INTERNAL)
            throw new CompilerException("Invalid type modifier: '%s' cannot combine with '%s'.", getTypeId(), mod);
        
        this.modifier = mod;
    }
    
    public final void insert(Type type) throws CompilerException
    {
        if(type.id == null)
            insertModifier(type.modifier);
        else if(type.modifier == null)
            insertTypeId(type.id);
        else throw new IllegalStateException();
    }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.TYPE; }

    @Override
    public final boolean isStatement() { return false; }
    
    @Override
    public final String toString()
    {
        TypeId type = getTypeId();
        TypeModifier mod = getModifier();
        
        if(type == TypeId.INT)
            return mod == TypeModifier.VAR
                    ? type.getTypeName()
                    : mod.getModifierName() + " " + type.getTypeName();
        
        return type.getTypeName();
    }
    
    public final boolean equals(Type other)
    {
        return getTypeId() == other.getTypeId() && getModifier() == other.getModifier();
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof Type)
            return equals((Type) o);
        return false;
    }
    
    @Override
    public final int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.modifier);
        return hash;
    }
    
    private static final HashMap<String, TypeId> TYPES = new HashMap<>();
    private static final HashMap<String, TypeModifier> MODS = new HashMap<>();
    static
    {
        for(TypeId type : TypeId.values())
            TYPES.put(type.getTypeName(), type);
        
        for(TypeModifier mod : TypeModifier.values())
            MODS.put(mod.getModifierName(), mod);
    }
    
    
    public static final Type fromName(String name)
    {
        TypeId type = TYPES.getOrDefault(name, null);
        if(type != null)
            return new Type(null, type);
        
        TypeModifier mod = MODS.getOrDefault(name, null);
        if(mod != null)
            return new Type(mod, null);
        
        return null;
    }
    
    public static final Type fromId(TypeId id) { return new Type(null, id); }
    
    public static final Type fromModifier(TypeModifier mod) { return new Type(mod, null); }
    
    public static final boolean exists(String name) { return fromName(name) != null; }
}
