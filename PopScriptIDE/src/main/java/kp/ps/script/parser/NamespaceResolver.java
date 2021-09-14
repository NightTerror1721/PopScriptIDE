/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.Macro;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.namespace.Namespace;
import kp.ps.script.namespace.NamespaceField;

/**
 *
 * @author Marc
 */
public final class NamespaceResolver extends ElementReference
{
    private final Identifier[] path;
    
    public NamespaceResolver(Statement base, Fragment identifier) throws SyntaxException
    {
        if(!identifier.isIdentifier())
            throw new SyntaxException("Expected a valid identifier after namespace resolver operator.");
        
        if(base.isNamespaceResolverOperation())
        {
            NamespaceResolver old = (NamespaceResolver) base;
            path = Arrays.copyOf(old.path, old.path.length + 1);
            path[path.length - 1] = (Identifier) identifier;
        }
        else if(base.isIdentifier())
        {
            path = new Identifier[] { (Identifier) base, (Identifier) identifier };
        }
        else throw new SyntaxException("Expected a valid identifier or chain namespace resolvers before namespace resolver operator.");
    }
    
    public final int size() { return path.length; }
    
    public final Identifier getIdentifier(int index) { return path[index]; }
    
    public final Identifier getLastIdentifier() { return path[path.length - 1]; }
    
    public final Namespace findNamespace(Namespace root) throws CompilerException
    {
        for(int i = 0; i < (path.length - 1); ++i)
        {
            if(!root.existsChild(path[i].toString()))
                throw new CompilerException("'" + root + "' namespace has not '" + path[i] + "' member.");
            root = root.getChild(path[i].toString());
        }
        return root;
    }
    
    public final NamespaceField getNamespaceField(Namespace root) throws CompilerException
    {
        Namespace namespace = findNamespace(root);
        String name = getLastIdentifier().toString();
        if(!namespace.existsField(name))
            throw new CompilerException("'" + name + "' identifier not found in '" + namespace + "' namespace.");
        return namespace.getField(name);
    }
    
    public final Macro getNamespaceMacro(Namespace root) throws CompilerException
    {
        Namespace namespace = findNamespace(root);
        String name = getLastIdentifier().toString();
        if(!namespace.existsMacro(name))
            throw new CompilerException("'" + name + "' identifier not found in '" + namespace + "' namespace.");
        return namespace.getMacro(name);
    }
    
    public final boolean existsAction(Namespace root) throws CompilerException
    {
        Namespace namespace = findNamespace(root);
        String name = getLastIdentifier().toString();
        if(!namespace.existsField(name))
            return false;
        NamespaceField field = namespace.getField(name);
        return field.isTypedValue() && field.getTypedValue().getType() == TypeId.ACTION;
    }
    
    @Override
    public FragmentType getFragmentType() { return FragmentType.NAMESPACE_RESOLVER; }

    @Override
    public final String toString()
    {
        return Stream.of(path)
                .map(Identifier::toString)
                .collect(Collectors.joining("."));
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof NamespaceResolver)
            return Arrays.equals(path, ((NamespaceResolver) o).path);
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Arrays.deepHashCode(this.path);
        return hash;
    }
}
