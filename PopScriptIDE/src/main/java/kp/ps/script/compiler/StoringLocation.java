/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.Objects;
import kp.ps.script.compiler.LocalElementsScope.Element;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.script.namespace.Namespace;
import kp.ps.script.namespace.NamespaceField;
import kp.ps.script.parser.Identifier;
import kp.ps.script.parser.NamespaceResolver;
import kp.ps.script.parser.Statement;

/**
 *
 * @author Marc
 */
public abstract class StoringLocation
{
    public abstract TypeId getType();
    
    public final boolean isFieldAssignable() { return getType().isFieldAssignable(); }
    
    public Element getLocalElement() { throw new IllegalStateException(); }
    
    public NamespaceField getNamespaceField() { throw new IllegalStateException(); }
    
    public boolean isEmpty() { return false; }
    public boolean isElement() { return false; }
    public boolean isNamespaceField() { return false; }
    
    public StatementValue toStatementValue() { throw new IllegalStateException(); }
    
    public static final StoringLocation of(Element element) throws CompilerException { return new ElementReturnLocation(element); }
    public static final StoringLocation of(NamespaceField field) throws CompilerException { return new NamespaceFieldReturnLocation(field); }
    public static final StoringLocation empty() { return new EmptyReturnLocation(); }
    
    public static final StoringLocation decode(CompilerState state, Identifier identifier) throws CompilerException
    {
        String name = identifier.getIdentifier();
        if(state.getLocalElements().exists(name))
            return of(state.getLocalElements().get(name));
        if(state.getRootNamespace().existsField(name))
            return of(state.getRootNamespace().getField(name));
        throw new CompilerException("'" + identifier + "' identifier not found.");
    }
    
    public static final StoringLocation decode(CompilerState state, NamespaceResolver resolver) throws CompilerException
    {
        Namespace namespace = resolver.findNamespace(state.getRootNamespace());
        String name = resolver.getLastIdentifier().getIdentifier();
        if(!namespace.existsField(name))
            throw new CompilerException("'" + name + "' identifier not found in '" + namespace + "' namespace.");
        return of(namespace.getField(name));
    }
    
    public static final StoringLocation decode(CompilerState state, Statement statement) throws CompilerException
    {
        switch(statement.getFragmentType())
        {
            case IDENTIFIER:
                return decode(state, (Identifier) statement);
            case NAMESPACE_RESOLVER:
                return decode(state, (NamespaceResolver) statement);
            default:
                throw new CompilerException("'" + statement + "' cannot contains a valid value.");
        }
    }
    
    
    private static final class ElementReturnLocation extends StoringLocation
    {
        private final Element element;
        
        private ElementReturnLocation(Element element) throws CompilerException
        {
            this.element = Objects.requireNonNull(element);
            if(element.isConstant())
                throw new CompilerException("Cannot store values into constants.");
        }
        
        @Override
        public final TypeId getType() { return element.getType(); }
        
        @Override
        public final Element getLocalElement() { return element; }
        
        @Override
        public final boolean isElement() { return true; }
        
        @Override
        public StatementValue toStatementValue() { return StatementValue.of(element); }
    }
    
    private static final class NamespaceFieldReturnLocation extends StoringLocation
    {
        private final NamespaceField field;
        
        private NamespaceFieldReturnLocation(NamespaceField field) throws CompilerException
        {
            this.field = Objects.requireNonNull(field);
            if(field.isConstant())
                throw new CompilerException("Cannot store values into constants.");
        }
        
        @Override
        public final TypeId getType() { return field.getType(); }
        
        @Override
        public final NamespaceField getNamespaceField() { return field; }
        
        @Override
        public final boolean isNamespaceField() { return true; }
        
        @Override
        public StatementValue toStatementValue() { return StatementValue.of(field); }
    }
    
    private static final class EmptyReturnLocation extends StoringLocation
    {
        private EmptyReturnLocation() {}
        
        @Override
        public final TypeId getType() { throw new IllegalStateException(); }
        
        @Override
        public final boolean isEmpty() { return true; }
    }
}
