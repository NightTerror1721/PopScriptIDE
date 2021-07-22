/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.TypeId;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public abstract class NamespaceField
{
    private final TypeId type;
    private final String name;
    
    protected NamespaceField(TypeId type, String name)
    {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
    }
    
    public final TypeId getType() { return type; }
    public final String getName() { return name; }
    
    public abstract NamespaceFieldType getFieldType();
    
    public final boolean isToken() { return getFieldType() == NamespaceFieldType.TOKEN; }
    public final boolean isInternal() { return getFieldType() == NamespaceFieldType.INTERNAL; }
    public final boolean isConstant() { return getFieldType() == NamespaceFieldType.CONSTANT; }
    
    public ScriptToken getToken() { throw new IllegalStateException(); }
    public ScriptInternal getInternal() { throw new IllegalStateException(); }
    public Int32 getValue() { throw new IllegalStateException(); }
    public NamespaceField getReferencedField() { throw new IllegalStateException(); }
    
    
    static final NamespaceField token(TypeId type, String name, ScriptToken token)
    {
        return new TokenNamespaceField(type, name, token);
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
