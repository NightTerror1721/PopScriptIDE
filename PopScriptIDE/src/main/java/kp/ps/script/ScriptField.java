/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script;

import java.util.Objects;
import kp.ps.utils.ints.Int32;
import kp.ps.utils.ints.UInt16;
import kp.ps.utils.ints.UInt32;

/**
 *
 * @author Marc
 */
public class ScriptField
{
    public static final ScriptField INVALID = new ScriptField(ScriptFieldType.INVALID, Int32.valueOf(3));
    public static final int BYTES = ScriptFieldType.BYTES + Int32.BYTES;
    
    private final ScriptFieldType type;
    private final Int32 value;
    
    private ScriptField(ScriptFieldType type, Int32 value)
    {
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
    }
    
    public final ScriptFieldType getType() { return type; }
    
    public final boolean isConstant() { return type == ScriptFieldType.CONSTANT; }
    public final boolean isUser() { return type == ScriptFieldType.USER; }
    public final boolean isInternal() { return type == ScriptFieldType.INTERNAL; }
    public final boolean isInvalid() { return type == ScriptFieldType.INVALID; }
    
    public final Int32 getValue() { return value; }
    public final Int32 getIndex() { return value; }
    
    public static final ScriptField constant(Int32 value) { return new ScriptField(ScriptFieldType.CONSTANT, value); }
    public static final ScriptField constant(int value) { return constant(Int32.valueOf(value)); }
    
    public static final ScriptField user(Int32 index) { return new ScriptField(ScriptFieldType.USER, index); }
    public static final ScriptField user(int index) { return user(Int32.valueOf(index)); }
    
    public static final ScriptField internal(UInt16 index) { return new ScriptField(ScriptFieldType.INTERNAL, Int32.valueOf(index.toInt())); }
    public static final ScriptField internal(int index) { return internal(UInt16.valueOf(index)); }
    
    public static final ScriptField invalid() { return INVALID; }
    
    
    public static final ScriptField read(byte[] buffer, int offset)
    {
        ScriptFieldType type = ScriptFieldType.fromValue(UInt32.read(buffer, offset));
        if(type == ScriptFieldType.INVALID)
            return INVALID;
        
        Int32 value = Int32.read(buffer, offset + UInt32.BYTES);
        return new ScriptField(type, value);
    }
    
    public static final void write(byte[] buffer, int offset, ScriptField field)
    {
        field = Objects.requireNonNullElse(field, INVALID);
        UInt32.write(buffer, offset, field.type.getValue());
        Int32.write(buffer, offset + UInt32.BYTES, field.value);
    }
}
