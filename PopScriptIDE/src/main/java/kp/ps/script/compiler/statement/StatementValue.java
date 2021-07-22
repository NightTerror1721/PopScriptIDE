/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement;

import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.FieldsManager.VariableIndex;
import kp.ps.script.compiler.LocalElementsScope.Element;
import kp.ps.script.compiler.TypeId;
import kp.ps.script.namespace.Namespace;
import kp.ps.script.namespace.NamespaceField;
import kp.ps.script.parser.Identifier;
import kp.ps.script.parser.Literal;
import kp.ps.script.parser.NamespaceResolver;
import kp.ps.script.parser.Statement;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public abstract class StatementValue implements StatementTask
{
    public static final StatementValue of(int value) { return new LiteralValue(Int32.valueOf(value)); }
    public static final StatementValue of(Int32 value) { return new LiteralValue(value); }
    public static final StatementValue of(Literal literal) { return new LiteralValue(literal.getValue()); }
    public static final StatementValue of(Element element) { return new LocalElementValue(element); }
    public static final StatementValue of(NamespaceField field) { return new NamespaceFieldValue(field); }
    
    public static final StatementValue decode(CompilerState state, Identifier identifier) throws CompilerException
    {
        String name = identifier.getIdentifier();
        if(state.getLocalElements().exists(name))
            return of(state.getLocalElements().get(name));
        if(state.getRootNamespace().existsField(name))
            return of(state.getRootNamespace().getField(name));
        throw new CompilerException("'" + identifier + "' identifier not found.");
    }
    
    public static final StatementValue decode(CompilerState state, NamespaceResolver resolver) throws CompilerException
    {
        Namespace namespace = resolver.findNamespace(state.getRootNamespace());
        String name = resolver.getLastIdentifier().getIdentifier();
        if(!namespace.existsField(name))
            throw new CompilerException("'" + name + "' identifier not found in '" + namespace + "' namespace.");
        return of(namespace.getField(name));
    }
    
    public static final StatementValue decode(CompilerState state, Statement statement) throws CompilerException
    {
        switch(statement.getFragmentType())
        {
            case LITERAL:
                return of((Literal) statement);
            case IDENTIFIER:
                return decode(state, (Identifier) statement);
            case NAMESPACE_RESOLVER:
                return decode(state, (NamespaceResolver) statement);
            default:
                throw new CompilerException("'" + statement + "' cannot contains a valid value.");
        }
    }
    
    
    private StatementValue() {}
            
    public abstract Kind getKind();
    public abstract Location getLocation();
    public abstract TypeId getType();
    
    public abstract VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) throws CompilerException;
    public abstract Int32 getConstantValue();
    public abstract ScriptInternal getInternal();
    public abstract ScriptToken getToken();
    
    public final boolean isConstant() { return getKind() == Kind.CONSTANT; }
    public final boolean isVariable() { return getKind() == Kind.VARIABLE; }
    public final boolean isInternal() { return getKind() == Kind.INTERNAL; }
    public final boolean isToken() { return getKind() == Kind.TOKEN; }
    
    public final VariableIndex getVariableIndexWithoutTemporalCreation()
    {
        try { return getVariableIndex(false); }
        catch(CompilerException ex) { throw new IllegalStateException(); }
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof StatementValue)
        {
            StatementValue val = (StatementValue) o;
            if(getKind() != val.getKind())
                return false;

            switch(getKind())
            {
                case CONSTANT:
                    return getConstantValue().equals(val.getConstantValue());
                case VARIABLE: {
                    VariableIndex vleft = getVariableIndexWithoutTemporalCreation();
                    if(vleft == null)
                        return false;
                    VariableIndex vright = val.getVariableIndexWithoutTemporalCreation();
                    if(vright == null)
                        return false;
                    return vleft.equals(vright);
                }
                case INTERNAL:
                    return getInternal() == val.getInternal();
                case TOKEN:
                    return getToken() == val.getToken();
                default:
                    throw new IllegalStateException();
            }
        }
        return false;
    }
    
    @Override
    public final StatementValue compile() { return this; }

    @Override
    public int hashCode() { return super.hashCode(); }
    
    
    public static enum Kind
    {
        VARIABLE,
        CONSTANT,
        INTERNAL,
        TOKEN
    }
    
    public static enum Location
    {
        LITERAL,
        LOCAL,
        NAMESPACE
    }
    
    
    private static final class LiteralValue extends StatementValue
    {
        private final Int32 value;
        
        private LiteralValue(Int32 value) { this.value = Objects.requireNonNull(value); }
        
        @Override
        public final Kind getKind() { return Kind.CONSTANT; }

        @Override
        public final Location getLocation() { return Location.LITERAL; }

        @Override
        public final TypeId getType() { return TypeId.INT; }

        @Override
        public final VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) { throw new IllegalStateException(); }

        @Override
        public final Int32 getConstantValue() { return value; }

        @Override
        public final ScriptInternal getInternal() { throw new IllegalStateException(); }

        @Override
        public final ScriptToken getToken() { throw new IllegalStateException(); }
    }
    
    private static final class LocalElementValue extends StatementValue
    {
        private final Element element;
        
        private LocalElementValue(Element element) { this.element = Objects.requireNonNull(element); }
        
        @Override
        public final Kind getKind()
        {
            if(element.isVariable())
                return Kind.VARIABLE;
            else if(element.isConstant())
                return Kind.CONSTANT;
            else if(element.isInternal())
                return Kind.INTERNAL;
            else if(element.isToken())
                return Kind.TOKEN;
            else throw new IllegalStateException();
        }

        @Override
        public final Location getLocation() { return Location.LOCAL; }

        @Override
        public final TypeId getType() { return element.getType(); }

        @Override
        public final VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) throws CompilerException
        {
            if(element.isVariable())
                return element.getVariableIndex(createTemporalIfItIsNeeded);
            throw new IllegalStateException();
        }

        @Override
        public final Int32 getConstantValue()
        {
            if(element.isConstant())
                return element.getConstantValue();
            throw new IllegalStateException();
        }

        @Override
        public final ScriptInternal getInternal()
        {
            if(element.isInternal())
                return element.getInternal();
            throw new IllegalStateException();
        }

        @Override
        public final ScriptToken getToken()
        {
            if(element.isToken())
                return element.getToken();
            throw new IllegalStateException();
        }
    }
    
    private static final class NamespaceFieldValue extends StatementValue
    {
        private final NamespaceField field;
        
        private NamespaceFieldValue(NamespaceField field) { this.field = Objects.requireNonNull(field); }
        
        @Override
        public final Kind getKind()
        {
            if(field.isConstant())
                return Kind.CONSTANT;
            else if(field.isInternal())
                return Kind.INTERNAL;
            else if(field.isToken())
                return Kind.TOKEN;
            else throw new IllegalStateException();
        }

        @Override
        public final Location getLocation() { return Location.NAMESPACE; }

        @Override
        public final TypeId getType() { return field.getType(); }

        @Override
        public final VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) { throw new IllegalStateException(); }

        @Override
        public final Int32 getConstantValue()
        {
            if(field.isConstant())
                return field.getValue();
            throw new IllegalStateException();
        }

        @Override
        public final ScriptInternal getInternal()
        {
            if(field.isInternal())
                return field.getInternal();
            throw new IllegalStateException();
        }

        @Override
        public final ScriptToken getToken()
        {
            if(field.isToken())
                return field.getToken();
            throw new IllegalStateException();
        }
    }
}
