/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script;

import kp.ps.utils.ints.UInt32;

/**
 *
 * @author Marc
 */
public enum ScriptFieldType
{
    CONSTANT(0),
    USER(1),
    INTERNAL(2),
    INVALID(3);
        
    private static final ScriptFieldType[] VALUES = values();
    private final UInt32 value;
    
    public static final int BYTES = UInt32.BYTES;
    
    private ScriptFieldType(int value)
    {
        this.value = UInt32.valueOf(value);
    }
    
    public final UInt32 getValue() { return value; }
    
    public static final ScriptFieldType fromValue(int value)
    {
        return value > 0 && value < VALUES.length ? VALUES[value] : INVALID;
    }
    
    public static final ScriptFieldType fromValue(UInt32 value)
    {
        return fromValue(value.toInt());
    }
}
