/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.HashMap;
import java.util.Objects;
import kp.ps.script.compiler.TypeId;

/**
 *
 * @author Marc
 */
public final class Type extends Fragment
{
    private final TypeId id;
    
    private Type(TypeId id) { this.id = id; }
    
    public final TypeId getTypeId() { return id; }
    
    public String getName() { return getTypeId().getTypeName(); }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.TYPE; }

    @Override
    public final boolean isStatement() { return false; }
    
    @Override
    public final String toString() { return getName(); }
    
    public final boolean isInt() { return getTypeId() == TypeId.INT; }
    public final boolean isState() { return getTypeId() == TypeId.STATE; }
    public final boolean isAction() { return getTypeId() == TypeId.ACTION; }
    public final boolean isTribe() { return getTypeId() == TypeId.TRIBE; }
    public final boolean isAttackTarget() { return getTypeId() == TypeId.ATTACK_TARGET; }
    public final boolean isAttackMode() { return getTypeId() == TypeId.ATTACK_MODE; }
    public final boolean isGuardMode() { return getTypeId() == TypeId.GUARD_MODE; }
    public final boolean isCountWildT() { return getTypeId() == TypeId.COUNT_WILD_T; }
    
    public final boolean equals(Type other) { return getTypeId() == other.getTypeId(); }
    
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
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(getTypeId());
        return hash;
    }
    
    
    
    private static final HashMap<String, Type> BY_NAME = new HashMap<>();
    private static final HashMap<TypeId, Type> BY_ID = new HashMap<>();
    static
    {
        for(TypeId id : TypeId.values())
        {
            Type t = new Type(id);
            BY_NAME.put(t.getName(), t);
            BY_ID.put(t.getTypeId(), t);
        }
    }
    
    
    public static final Type fromName(String name) { return BY_NAME.getOrDefault(name, null); }
    public static final Type fromId(TypeId id) { return BY_ID.getOrDefault(id, null); }
    
    public static final boolean exists(String name) { return fromName(name) != null; }
    public static final boolean exists(TypeId id) { return fromId(id) != null; }
}
