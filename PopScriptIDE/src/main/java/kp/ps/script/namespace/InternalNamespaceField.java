/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.compiler.TypeId;

/**
 *
 * @author Marc
 */
final class InternalNamespaceField extends NamespaceField
{
    private final ScriptInternal internal;
    
    InternalNamespaceField(String name, ScriptInternal internal)
    {
        super(name);
        this.internal = Objects.requireNonNull(internal);
    }
    
    @Override
    public final NamespaceFieldType getFieldType() { return NamespaceFieldType.INTERNAL; }
    
    @Override
    public final TypeId getType() { return TypeId.INT; }
    
    @Override
    public final ScriptInternal getInternal() { return internal; }
}
