/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.types.CompleteType;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.compiler.types.TypeModifier;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
final class ConstNamespaceField extends NamespaceField
{
    private Int32 value;
    
    ConstNamespaceField(String name)
    {
        super(name);
    }
    
    @Override
    public final NamespaceFieldType getFieldType()
    {
        return NamespaceFieldType.CONSTANT;
    }
    
    @Override
    public final TypeId getType() { return TypeId.INT; }
    
    @Override
    public final CompleteType getCompleteType() throws CompilerException { return getType().complete(TypeModifier.CONST); }
    
    @Override
    public final Int32 getValue() throws CompilerException
    {
        if(!isInitiated())
            throw new CompilerException("const int %s value is not initiated", getName());
        return value;
    }
    
    @Override
    public final boolean isInitiated() { return value != null; }
    
    @Override
    public final void initiateConstant(Int32 value) throws CompilerException
    {
        if(isInitiated())
            throw new CompilerException("const int %s value already initiated", getName());
        this.value = Objects.requireNonNull(value);
    }
}
