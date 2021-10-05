/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.decompiler;

import java.util.Objects;
import kp.ps.script.ScriptField;
import kp.ps.script.ScriptFieldType;
import kp.ps.script.ScriptInternal;
import kp.ps.utils.ints.UInt16;

/**
 *
 * @author Marc
 */
public abstract class Field
{
    private final int fieldIndex;
    private final String name;
    
    private Field(int fieldIndex, String name)
    {
        this.fieldIndex = fieldIndex;
        this.name = Objects.requireNonNull(name);
    }
    
    public static final Field valueOf(int fieldIndex, ScriptField field)
    {
        if(field == null)
            return new InvalidField(fieldIndex);
        
        switch(field.getType())
        {
            case USER:
                return new UserField(fieldIndex, field.getIndex().toInt());
                
            case CONSTANT:
                return new ConstantField(fieldIndex, field.getValue().toInt());
                
            case INTERNAL: {
                ScriptInternal internal = ScriptInternal.fromCode(UInt16.valueOf(field.getValue()));
                if(internal == null)
                    return new InvalidField(fieldIndex);
                return new InternalField(fieldIndex, internal);
            }
            
            default:
                return new InvalidField(fieldIndex);
        }
    }
    
    public static final Field invalid(int fieldIndex) { return valueOf(fieldIndex, null); }
    
    public final int getFieldIndex() { return fieldIndex; }
    
    public final boolean isInvalid() { return getFieldType() == ScriptFieldType.INVALID; }
    public final boolean isUser() { return getFieldType() == ScriptFieldType.USER; }
    public final boolean isInternal() { return getFieldType() == ScriptFieldType.INTERNAL; }
    public final boolean isConstant() { return getFieldType() == ScriptFieldType.CONSTANT; }
    
    public abstract ScriptFieldType getFieldType();
    
    public int getConstantValue() { throw new IllegalStateException(); }
    
    @Override
    public final String toString() { return name; }
    
    @Override
    public final boolean equals(Object o)
    {
        if(o == this)
            return true;
        
        if(o == null)
            return false;
        
        if(o instanceof Field)
            return fieldIndex == ((Field) o).fieldIndex;
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 5;
        hash = 97 * hash + this.fieldIndex;
        return hash;
    }
    
    
    private static final class UserField extends Field
    {
        private UserField(int fieldIndex, int varIndex)
        {
            super(fieldIndex, "variable_" + varIndex);
        }
        
        @Override
        public final ScriptFieldType getFieldType() { return ScriptFieldType.USER; }
    }
    
    private static final class ConstantField extends Field
    {
        private final int value;
        
        private ConstantField(int fieldIndex, int value)
        {
            super(fieldIndex, Integer.toString(value));
            this.value = value;
        }
        
        @Override
        public final ScriptFieldType getFieldType() { return ScriptFieldType.CONSTANT; }
        
        @Override
        public final int getConstantValue() { return value; }
    }
    
    private static final class InternalField extends Field
    {
        private InternalField(int fieldIndex, ScriptInternal internal)
        {
            super(fieldIndex, internal.getInternalName());
        }
        
        @Override
        public final ScriptFieldType getFieldType() { return ScriptFieldType.INTERNAL; }
    }
    
    private static final class InvalidField extends Field
    {
        private InvalidField(int fieldIndex)
        {
            super(fieldIndex, "<invalid-field>");
        }
        
        @Override
        public final ScriptFieldType getFieldType() { return ScriptFieldType.INVALID; }
    }
}
