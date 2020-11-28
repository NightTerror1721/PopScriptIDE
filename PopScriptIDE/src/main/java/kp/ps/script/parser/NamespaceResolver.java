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
public final class NamespaceResolver extends Statement
{
    private final Identifier identifier;
    private final NamespaceResolver baseResolver;
    private final Identifier base;
    
    public NamespaceResolver(Statement base, Fragment identifier) throws SyntaxException
    {
        if(!identifier.isIdentifier())
            throw new SyntaxException("Expected a valid identifier after namespace resolver operator.");
        this.identifier = (Identifier) identifier;
        
        if(base.isNamespaceResolverOperation())
        {
            this.baseResolver = (NamespaceResolver) base;
            this.base = null;
        }
        else if(base.isIdentifier())
        {
            this.baseResolver = null;
            this.base = (Identifier) base;
        }
        else throw new SyntaxException("Expected a valid identifier or chain namespace resolvers before namespace resolver operator.");
    }
    
    public final boolean isChain() { return baseResolver != null; }
    
    public final Identifier getIdentifier() { return identifier; }
    
    public final Identifier getBase()
    {
        if(isChain())
            throw new UnsupportedOperationException();
        return base;
    }
    
    public final NamespaceResolver getBaseResolver()
    {
        if(!isChain())
            throw new UnsupportedOperationException();
        return baseResolver;
    }
    
    @Override
    public FragmentType getFragmentType() { return FragmentType.NAMESPACE_RESOLVER; }

    @Override
    public final String toString()
    {
        if(base == null)
            return baseResolver + "." + identifier;
        return base + "." + identifier;
    }
    
}
