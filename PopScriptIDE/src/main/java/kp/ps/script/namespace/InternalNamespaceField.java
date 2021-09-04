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

/**
 *
 * @author Marc
 */
final class InternalNamespaceField extends NamespaceField
{
    private ScriptInternal internal;
    
    InternalNamespaceField(String name)
    {
        super(name);
    }
    
    @Override
    public final NamespaceFieldType getFieldType()
    {
        return NamespaceFieldType.INTERNAL;
    }
    
    @Override
    public final TypeId getType() { return TypeId.INT; }
    
    @Override
    public final ScriptInternal getInternal() throws CompilerException
    {
        if(!isInitiated())
            throw new CompilerException("internal int %s value is not initiated", getName());
        return internal;
    }
    
    @Override
    public final boolean isInitiated() { return internal != null; }
    
    @Override
    public final void initiateInternal(ScriptInternal value) throws CompilerException
    {
        if(isInitiated())
            throw new CompilerException("internal int %s value already initiated", getName());
        this.internal = Objects.requireNonNull(value);
    }
}
