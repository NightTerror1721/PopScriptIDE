/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
final class AliasNamespaceField extends NamespaceField
{
    private final NamespaceField reference;
    
    AliasNamespaceField(String name, NamespaceField reference)
    {
        super(reference.getDataType(), name);
        this.reference = Objects.requireNonNull(reference);
    }
    
    @Override
    public final NamespaceFieldType getFieldType() { return reference.getFieldType(); }
    
    @Override
    public final boolean isAlias() { return true; }
    
    @Override
    public final ScriptToken getToken() { return reference.getToken(); }
    
    @Override
    public final ScriptInternal getInternal() { return reference.getInternal(); }
    
    @Override
    public final Int32 getValue() { return reference.getValue(); }

    @Override
    public final NamespaceField getReferencedField() { return reference; }
}
