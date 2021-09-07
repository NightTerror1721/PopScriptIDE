/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.TypedValue;
import kp.ps.script.compiler.types.CompleteType;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.compiler.types.TypeModifier;

/**
 *
 * @author Marc
 */
final class TypedValueNamespaceField extends NamespaceField
{
    private final TypeId type;
    private TypedValue value;
    
    TypedValueNamespaceField(String name, TypeId type)
    {
        super(name);
        this.type = Objects.requireNonNull(type);
    }
    
    @Override
    public final NamespaceFieldType getFieldType() { return NamespaceFieldType.TYPED_VALUE; }
    
    @Override
    public final TypeId getType() { return type; }
    
    @Override
    public final CompleteType getCompleteType() throws CompilerException { return getType().complete(TypeModifier.INTERNAL); }
    
    @Override
    public final TypedValue getTypedValue() throws CompilerException
    {
        if(!isInitiated())
                throw new CompilerException("internal %s %s value is not initiated", type, getName());
        return value;
    }
    
    @Override
    public final boolean isInitiated() { return value != null; }
    
    @Override
    public final void initiateTypedValue(TypedValue value) throws CompilerException
    {
        if(isInitiated())
                throw new CompilerException("internal %s %s value already initiated", type, getName());
        
        if(type != null && type != value.getType())
                throw new CompilerException("Cannot assign value from %s type to %s.", value.getType(), type);
        
        this.value = value;
    }
}
