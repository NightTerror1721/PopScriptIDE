/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.compiler.TypeId;
import kp.ps.script.compiler.TypedValue;

/**
 *
 * @author Marc
 */
final class TypedValueNamespaceField extends NamespaceField
{
    private TypedValue value;
    
    TypedValueNamespaceField(String name, TypedValue value)
    {
        super(name);
        this.value = Objects.requireNonNull(value);
    }
    
    @Override
    public final NamespaceFieldType getFieldType() { return NamespaceFieldType.TOKEN; }
    
    @Override
    public final TypeId getType() { return value.getType(); }
    
    @Override
    public final TypedValue getTypedValue() { return value; }
}
