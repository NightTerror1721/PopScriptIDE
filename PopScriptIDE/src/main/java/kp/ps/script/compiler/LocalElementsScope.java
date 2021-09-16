/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;
import kp.ps.script.ScriptInternal;
import kp.ps.script.compiler.FieldsManager.VariableIndex;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.script.compiler.types.CompleteType;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.compiler.types.TypeModifier;
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
        
        elements.values().stream()
                .filter(Predicate.not(Element::isArgument))
                .filter(Element::isVariable)
                .filter(Element::isVariableInitiated)
                .forEach(v -> {
                    try { fieldsManager.popVariables(v.getVariableIndex(false)); }
                    catch(CompilerException ex) { throw new IllegalStateException(); }
                });
        
        elements.clear();
    }
    
    public final Element pushTemporal() throws CompilerException
    {
        VariableElement elem = new VariableElement("<temporal:" + temporals.size() + ">");
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
        VariableElement temp = temporals.pop();
        if(temp.isVariableInitiated())
            fieldsManager.popVariables(temp.variableIndex);
    }
    
    public final Element createConstant(String identifier) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new IllegalStateException();
        
        if(!temporals.isEmpty())
            throw new IllegalStateException("Cannot create variable if temporals it's not empty.");
        
        ConstElement elem = new ConstElement(identifier);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createVariable(String identifier) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new CompilerException("Duplicated identifier '%s'.", identifier);
        
        VariableElement elem = new VariableElement(identifier);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createInternal(String identifier) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new CompilerException("Duplicated identifier '%s'.", identifier);
        
        InternalElement elem = new InternalElement(identifier);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createTypedValue(String identifier, TypeId type) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new CompilerException("Duplicated identifier '%s'.", identifier);
        
        TypedValueElement elem = new TypedValueElement(type, identifier);
        elements.put(identifier, elem);
        return elem;
    }
    
    public final Element createArgument(String identifier, StatementValue value) throws CompilerException
    {
        if(elements.containsKey(identifier))
            throw new CompilerException("Duplicated identifier '%s'.", identifier);
        
        ArgumentElement elem = new ArgumentElement(value);
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
        
        public abstract CompleteType getCompleteType() throws CompilerException;
        
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
        
        boolean isArgument() { return false; }
        
        public final MemoryAddress toMemoryAddress() throws CompilerException { return MemoryAddress.of(this); }
    }
    
    private final class ConstElement extends Element
    {
        private final String name;
        private Int32 value;
        
        private ConstElement(String name)
        {
            this.name = name;
        }
        
        @Override
        public final CompleteType getCompleteType() throws CompilerException { return getType().complete(TypeModifier.CONST); }
        
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
        
        @Override
        public final String toString()
        {
            if(name == null)
            {
                if(value != null)
                    return "(const int) " + value;
                return "(const int) <undefined>";
            }
            else
            {
                if(value == null)
                    return "const int " + name;
                return "const int " + name + " = " + value;
            }
        }
    }
    
    private final class VariableElement extends Element
    {
        private final String name;
        private VariableIndex variableIndex;
        
        private VariableElement(String name) throws CompilerException
        {
            this.name = Objects.requireNonNull(name);
            this.variableIndex = null;
        }
        
        @Override
        public final CompleteType getCompleteType() throws CompilerException { return getType().complete(TypeModifier.VAR); }
        
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
        
        @Override
        public final String toString()
        {
            return "int " + name;
        }
    }
    
    private final class InternalElement extends Element
    {
        private final String name;
        private ScriptInternal internal;
        
        private InternalElement(String name)
        {
            this.name = Objects.requireNonNull(name);
        }
        
        @Override
        public final CompleteType getCompleteType() throws CompilerException { return getType().complete(TypeModifier.INTERNAL); }
        
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
        
        @Override
        public final String toString()
        {
            if(internal == null)
                return "internal int " + name;
            return "internal int " + name + " = " + internal.getInternalName();
        }
    }
    
    private final class TypedValueElement extends Element
    {
        private final String name;
        private final TypeId type;
        private TypedValue value;
        
        private TypedValueElement(TypeId type, String name)
        {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
        }
        
        @Override
        public final CompleteType getCompleteType() throws CompilerException { return getType().complete(TypeModifier.INTERNAL); }
        
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
        
        @Override
        public final String toString()
        {
            if(value == null)
                return type.getTypeName() + " " + name;
            return type.getTypeName() + " " + name + " = " + value.getName();
        }
    }
    
    private final class ArgumentElement extends Element
    {
        private final StatementValue value;
        
        private ArgumentElement(StatementValue value)
        {
            this.value = Objects.requireNonNull(value);
        }
        
        @Override
        public TypeId getType() { return value.getType(); }
        
        @Override
        public final CompleteType getCompleteType() throws CompilerException { return value.getCompleteType(); }
        
        @Override
        public VariableIndex getVariableIndex(boolean createTemporalIfItIsNeeded) throws CompilerException
        {
            return value.getVariableIndex(createTemporalIfItIsNeeded);
        }
        
        @Override
        public Int32 getConstantValue() throws CompilerException { return value.getConstantValue(); }
        
        @Override
        public ScriptInternal getInternal() throws CompilerException { return value.getInternal(); }
        
        @Override
        public TypedValue getTypedValue() throws CompilerException { return value.getTypedValue(); }
        
        @Override
        public boolean isConstant() { return value.isConstant(); }
        
        @Override
        public boolean isVariable() { return value.isVariable(); }
        
        @Override
        public boolean isInternal() { return value.isInternal(); }
        
        @Override
        public boolean isTypedValue() { return value.isTypedValue(); }
        
        @Override
        public boolean isVariableInitiated() { return value.isVariableInitiated(); }
        
        @Override
        public boolean isConstInitiated() { return value.isInitiated(); }
        
        @Override
        public void initiateConstConstantValue(Int32 value) throws CompilerException { this.value.initiateConstant(value); }
        
        @Override
        public void initiateConstInternalValue(ScriptInternal value) throws CompilerException { this.value.initiateInternal(value); }
        
        @Override
        public void initiateConstTypedValueValue(TypedValue value) throws CompilerException { this.value.initiateTypedValue(value); }
        
        @Override
        final boolean isArgument() { return true; }
        
        @Override
        public final String toString() { return value.toString(); }
    }
}
