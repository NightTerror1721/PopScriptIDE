/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.TypeId;
import kp.ps.script.compiler.TypedValue;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public abstract class NamespaceField
{
    private final String name;
    
    protected NamespaceField(String name)
    {
        this.name = Objects.requireNonNull(name);
    }
    
    public abstract TypeId getType();
    public final String getName() { return name; }
    
    public abstract NamespaceFieldType getFieldType();
    
    public final boolean isTypedValue() { return getFieldType() == NamespaceFieldType.TOKEN; }
    public final boolean isInternal() { return getFieldType() == NamespaceFieldType.INTERNAL; }
    public final boolean isConstant() { return getFieldType() == NamespaceFieldType.CONSTANT; }
    
    public TypedValue getTypedValue() { throw new IllegalStateException(); }
    public ScriptInternal getInternal() { throw new IllegalStateException(); }
    public Int32 getValue() { throw new IllegalStateException(); }
    
    public final MemoryAddress toMemoryAddress() throws CompilerException { return MemoryAddress.of(this); }
    
    
    static final NamespaceField typedValue(String name, TypedValue value)
    {
        return new TypedValueNamespaceField(name, value);
    }
    
    static final NamespaceField internal(String name, ScriptInternal internal)
    {
        return new InternalNamespaceField(name, internal);
    }
    
    public static final NamespaceField constant(String name, Int32 value)
    {
        return new ConstantNamespaceField(name, value);
    }
}
