/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.compiler.TypeId;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
final class ConstantNamespaceField extends NamespaceField
{
    private final Int32 value;
    
    ConstantNamespaceField(String name, Int32 value)
    {
        super(TypeId.INT, name);
        this.value = Objects.requireNonNull(value);
    }
    
    @Override
    public NamespaceFieldType getFieldType() { return NamespaceFieldType.CONSTANT; }

    @Override
    public final Int32 getValue() { return value; }
}
