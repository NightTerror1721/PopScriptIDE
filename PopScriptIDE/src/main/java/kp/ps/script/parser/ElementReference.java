/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

/**
 *
 * @author Marc
 */
public abstract class ElementReference extends Statement
{
    @Override
    public final boolean isElementReference() { return true; }
    
    public final Identifier getIdentifier()
    {
        if(!isIdentifier())
            throw new IllegalStateException();
        return (Identifier) this;
    }
    
    public final NamespaceResolver getNamespaceResolver()
    {
        if(!isNamespaceResolverOperation())
            throw new IllegalStateException();
        return (NamespaceResolver) this;
    }
}
