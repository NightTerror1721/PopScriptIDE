/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

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
    COUNT_WILD_T("count_wild_t");
    
    private final String name;
    
    private TypeId(String name)
    {
        this.name = name;
    }
    
    public final String getTypeName() { return name; }
    
    @Override
    public final String toString() { return name; }
}
