/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.namespace;

import java.util.Objects;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.TypeId;

/**
 *
 * @author Marc
 */
final class TokenNamespaceField extends NamespaceField
{
    private final ScriptToken token;
    
    TokenNamespaceField(TypeId type, String name, ScriptToken token)
    {
        super(type, name);
        this.token = Objects.requireNonNull(token);
        
        if(type == TypeId.INT)
            throw new IllegalArgumentException("Token namespace field cannot be a int type");
    }
    
    @Override
    public final NamespaceFieldType getFieldType() { return NamespaceFieldType.TOKEN; }
    
    @Override
    public final ScriptToken getToken() { return token; }
}
