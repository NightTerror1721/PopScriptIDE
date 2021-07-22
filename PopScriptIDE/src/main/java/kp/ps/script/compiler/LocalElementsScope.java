/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.FieldsManager.VariableIndex;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class LocalElementsScope
{
    private final FieldsManager fieldsManager;
    private final HashMap<String, Element> elements = new HashMap<>();
    private final HashMap<Int32, ConstantElement> literals = new HashMap<>();
    private final LinkedList<VariableElement> temporals = new LinkedList<>();
    private final LocalElementsScope parent;
    
    public LocalElementsScope(FieldsManager fieldsManager)
    {
        this.fieldsManager = Objects.requireNonNull(fieldsManager);
        this.parent = null;
    }
    private LocalElementsScope(LocalElementsScope parent)
    {
        this.fieldsManager = parent.fieldsManager;
        this.parent = parent;
    }
    
    public final LocalElementsScope createChild()
    {
        return new LocalElementsScope(this);
    }
    
    public final LocalElementsScope getParent() { return parent; }
    
    public final boolean isRoot() { return parent == null; }
    
    public final Element registerLiteral(Int32 value) throws CompilerException
    {
        if(literals.containsKey(value))
            return literals.get(value);
        
        ConstantElement elem = new ConstantElement(value);
        literals.put(value, elem);
        return elem;
    }
    
    public final Element pushTemporal() throws CompilerException
    {
        VariableElement elem = new VariableElement(true);
        temporals.push(elem);
        return elem;
    }
    
    public final Element peekTemporal()
    {
        if(temporals.isEmpty())
            throw new IllegalStateException();
        return temporals.peek();
    }
    
    public final void popTemporal()
    {
        if(temporals.isEmpty())
            throw new IllegalStateException();
        temporals.pop();
        fieldsManager.popVariables(1);
    }
    
    public final Element createConstant(String identifier, Int32 value) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        if(!temporals.isEmpty())
            throw new IllegalStateException("Cannot create variable if temporals it's not empty.");
        
        ConstantElement elem = (ConstantElement) registerLiteral(value);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createVariable(String identifier) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        VariableElement elem = new VariableElement(false);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createInternal(String identifier, ScriptInternal internal) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        InternalElement elem = new InternalElement(internal);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createToken(String identifier, ScriptToken token, TypeId type)
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        TokenElement elem = new TokenElement(token, type);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final boolean exists(String identifier)
    {
        if(elements.containsKey(identifier))
            return true;
        return parent != null ? parent.exists(identifier) : false;
    }
    
    public final Element get(String identifier)
    {
        Element elem = elements.get(identifier);
        if(elem == null)
        {
            if(parent == null)
                throw new IllegalStateException();
            return parent.get(identifier);
        }
        return elem;
    }
    
    
    
    public abstract class Element
    {
        private final TypeId type;
        
        private Element(TypeId type)
        {
            this.type = Objects.requireNonNull(type);
        }
        
        public final TypeId getType() { return type; }
        
        public VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) throws CompilerException { throw new IllegalStateException(); }
        public Int32 getConstantValue() { throw new IllegalStateException(); }
        public ScriptInternal getInternal() { throw new IllegalStateException(); }
        public ScriptToken getToken() { throw new IllegalStateException(); }
        
        public boolean isConstant() { return false; }
        public boolean isVariable() { return false; }
        public boolean isInternal() { return false; }
        public boolean isToken() { return false; }
    }
    
    private final class ConstantElement extends Element
    {
        private final Int32 value;
        
        private ConstantElement(Int32 value)
        {
            super(TypeId.INT);
            this.value = Objects.requireNonNull(value);
        }
        
        @Override
        public final boolean isConstant() { return true; }
        
        @Override
        public Int32 getConstantValue() { return value; }
    }
    
    private final class VariableElement extends Element
    {
        private VariableIndex variableIndex;
        
        private VariableElement(boolean isTemporal) throws CompilerException
        {
            super(TypeId.INT);
            this.variableIndex = isTemporal ? fieldsManager.newVariable() : null;
        }
        
        @Override
        public final VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) throws CompilerException
        {
            if(variableIndex == null && createTemporalIfItIsNeeded)
                variableIndex = fieldsManager.newVariable();
            return variableIndex;
        }
        
        @Override
        public final boolean isVariable() { return true; }
    }
    
    private final class InternalElement extends Element
    {
        private final ScriptInternal internal;
        
        private InternalElement(ScriptInternal internal)
        {
            super(TypeId.INT);
            this.internal = Objects.requireNonNull(internal);
        }
        
        @Override
        public final boolean isInternal() { return true; }
        
        @Override
        public final ScriptInternal getInternal() { return internal; }
    }
    
    private final class TokenElement extends Element
    {
        private final ScriptToken token;
        
        private TokenElement(ScriptToken token, TypeId type)
        {
            super(type);
            this.token = Objects.requireNonNull(token);
            if(type.isFieldAssignable())
                throw new IllegalStateException();
        }
        
        @Override
        public final ScriptToken getToken() { return token; }
        
        @Override
        public final boolean isToken() { return true; }
    }
}
