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
import kp.ps.script.compiler.FieldsManager.VariableIndex;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class LocalElementsScope
{
    private final FieldsManager fieldsManager;
    private final HashMap<String, Element> elements = new HashMap<>();
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
    
    public final void clear()
    {
        if(!temporals.isEmpty())
            throw new IllegalStateException();
        
        int count = (int) elements.values().stream()
                .filter(Element::isVariable)
                .count();
        fieldsManager.popVariables(count);
        
        elements.clear();
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
    
    public final Element createConstant(String identifier) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        if(!temporals.isEmpty())
            throw new IllegalStateException("Cannot create variable if temporals it's not empty.");
        
        ConstElement elem = new ConstElement();
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
    
    public final Element createInternal(String identifier)
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        InternalElement elem = new InternalElement();
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createTypedValue(String identifier, TypeId type)
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        TypedValueElement elem = new TypedValueElement(type);
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
        private Element() {}
        
        public TypeId getType() { return TypeId.INT; }
        
        public VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) throws CompilerException { throw new IllegalStateException(); }
        public Int32 getConstantValue() throws CompilerException { throw new IllegalStateException(); }
        public ScriptInternal getInternal() throws CompilerException { throw new IllegalStateException(); }
        public TypedValue getTypedValue() throws CompilerException { throw new IllegalStateException(); }
        
        public boolean isConstant() { return false; }
        public boolean isVariable() { return false; }
        public boolean isInternal() { return false; }
        public boolean isTypedValue() { return false; }
        
        public boolean isVariableInitiated() { throw new IllegalStateException(); }
        
        public boolean isConstInitiated() { throw new IllegalStateException(); }
        
        public void initiateConstConstantValue(Int32 value) throws CompilerException { throw new IllegalStateException(); }
        public void initiateConstInternalValue(ScriptInternal value) throws CompilerException { throw new IllegalStateException(); }
        public void initiateConstTypedValueValue(TypedValue value) throws CompilerException { throw new IllegalStateException(); }
        
        protected boolean isAlias() { return false; }
        
        public final MemoryAddress toMemoryAddress() throws CompilerException { return MemoryAddress.of(this); }
    }
    
    private final class ConstElement extends Element
    {
        private Int32 value;
        
        private ConstElement() {}
        
        @Override
        public final boolean isConstant() { return true; }
        
        @Override
        public final Int32 getConstantValue() throws CompilerException
        {
            if(!isConstInitiated())
                throw new CompilerException("const int value is not initiated");
            return value;
        }
        
        @Override
        public final boolean isConstInitiated() { return value != null; }
        
        @Override
        public final void initiateConstConstantValue(Int32 value) throws CompilerException
        {
            if(isConstInitiated())
                throw new CompilerException("const int value already initiated");
            this.value = Objects.requireNonNull(value);
        }
    }
    
    private final class VariableElement extends Element
    {
        private VariableIndex variableIndex;
        
        private VariableElement(boolean isTemporal) throws CompilerException
        {
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
        
        @Override
        public final boolean isVariableInitiated() { return variableIndex != null; }
    }
    
    private final class InternalElement extends Element
    {
        private ScriptInternal internal;
        
        private InternalElement() {}
        
        @Override
        public final boolean isInternal() { return true; }
        
        @Override
        public final ScriptInternal getInternal() throws CompilerException
        {
            if(!isConstInitiated())
                throw new CompilerException("internal int value is not initiated");
            return internal;
        }
        
        @Override
        public final boolean isConstInitiated() { return internal != null; }
        
        @Override
        public final void initiateConstInternalValue(ScriptInternal value) throws CompilerException
        {
            if(isConstInitiated())
                throw new CompilerException("internal int value already initiated");
            this.internal = Objects.requireNonNull(value);
        }
    }
    
    private final class TypedValueElement extends Element
    {
        private final TypeId type;
        private TypedValue value;
        
        private TypedValueElement(TypeId type)
        {
            this.type = Objects.requireNonNull(type);
        }
        
        @Override
        public final TypeId getType() { return value.getType(); }
        
        @Override
        public final TypedValue getTypedValue() throws CompilerException
        {
            if(!isConstInitiated())
                throw new CompilerException("internal %s value is not initiated", type);
            return value;
        }
        
        @Override
        public final boolean isTypedValue() { return true; }
        
        @Override
        public final boolean isConstInitiated() { return value != null; }
        
        @Override
        public final void initiateConstTypedValueValue(TypedValue value) throws CompilerException
        {
            if(isConstInitiated())
                throw new CompilerException("internal %s value already initiated", type);
            
            if(type != null && type != value.getType())
                throw new CompilerException("Cannot assign value from %s type to %s.", value.getType(), type);
            
            this.value = Objects.requireNonNull(value);
        }
    }
}
