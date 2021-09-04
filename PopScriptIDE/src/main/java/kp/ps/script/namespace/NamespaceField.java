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
    
    public final boolean isTypedValue() { return getFieldType() == NamespaceFieldType.TYPED_VALUE; }
    public final boolean isInternal() { return getFieldType() == NamespaceFieldType.INTERNAL; }
    public final boolean isConstant() { return getFieldType() == NamespaceFieldType.CONSTANT; }
    
    public TypedValue getTypedValue() throws CompilerException { throw new IllegalStateException(); }
    public ScriptInternal getInternal() throws CompilerException { throw new IllegalStateException(); }
    public Int32 getValue() throws CompilerException { throw new IllegalStateException(); }
    
    public abstract boolean isInitiated();
    
    public void initiateTypedValue(TypedValue value) throws CompilerException
    {
        throw new CompilerException("Cannot assign a %s value with %s type.", value.getType(), getType());
    }
    public void initiateInternal(ScriptInternal value) throws CompilerException
    {
        throw new CompilerException("Cannot assign a int value with %s type.", getType());
    }
    public void initiateConstant(Int32 value) throws CompilerException
    {
        throw new CompilerException("Cannot assign a int value with %s type.", getType());
    }
    
    public final MemoryAddress toMemoryAddress() throws CompilerException { return MemoryAddress.of(this); }
    
    
    public static final NamespaceField typedValue(String name, TypeId type)
    {
        if(type == null || type == TypeId.INT)
            throw new IllegalStateException();
        return new TypedValueNamespaceField(name, type);
    }
    
    public static final NamespaceField internal(String name)
    {
        return new InternalNamespaceField(name);
    }
    
    public static final NamespaceField constant(String name)
    {
        return new ConstNamespaceField(name);
    }
}
