/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.types;

import kp.ps.script.compiler.CompilerException;

/**
 *
 * @author Marc
 */
public enum TypeId
{
    INT("int"),
    STATE("state"),
    ACTION("action"),
    TRIBE("tribe"),
    ATTACK_TARGET("attack_target"),
    ATTACK_MODE("attack_mode"),
    GUARD_MODE("guard_mode"),
    COUNT_WILD_T("count_wild_t"),
    SHOT_TYPE("shot_type"),
    VEHICLE_TYPE("vehicle_type");
    
    private final String name;
    
    private TypeId(String name)
    {
        this.name = name;
    }
    
    public final String getTypeName() { return name; }
    
    public final boolean isFieldAssignable() { return this == INT; }
    
    @Override
    public final String toString() { return name; }
    
    public final CompleteType complete() throws CompilerException
    {
        return new CompleteType(null, this);
    }
    
    public final CompleteType complete(TypeModifier modifier) throws CompilerException
    {
        return new CompleteType(modifier, this);
    }
    
    public static final CompleteType complete(TypeModifier modifier, TypeId type) throws CompilerException
    {
        return new CompleteType(modifier, type);
    }
}
