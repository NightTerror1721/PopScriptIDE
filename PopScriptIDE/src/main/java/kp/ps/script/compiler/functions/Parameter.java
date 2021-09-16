/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.functions;

import java.util.Objects;
import kp.ps.script.ScriptInternal;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.TypedValue;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.script.compiler.types.CompleteType;
import kp.ps.script.compiler.types.ParameterType;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.compiler.types.TypeModifier;
import kp.ps.script.namespace.Namespace;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public abstract class Parameter
{
    private final String name;

    private Parameter(String name)
    {
        this.name = Objects.requireNonNull(name);
    }

    public final String getName() { return name; }
    
    public final boolean isInteger()
    {
        ParameterType type = getType();
        return type.getType() == TypeId.INT && (!type.hasModifier() || type.getModifier() != TypeModifier.INTERNAL);
    }
    
    public final boolean isInternal()
    {
        ParameterType type = getType();
        return type.getType() == TypeId.INT && (!type.hasModifier() || type.getModifier() == TypeModifier.INTERNAL);
    }
    
    public final boolean isTypedValue()
    {
        return getType().getType() != TypeId.INT;
    }

    public abstract ParameterType getType();

    public abstract boolean hasDefaultValue();
    
    public abstract StatementValue checkOrGetDefault(StatementValue value) throws CompilerException;
    
    public abstract StatementValue compile(CompilerState state, CodeManager code, StatementValue value) throws CompilerException;
    public abstract Int32 constCompile(Int32 value) throws CompilerException;

    public Int32 getDefaultIntegerValue() { throw new IllegalStateException(); }
    public ScriptInternal getDefaultInternalValue() { throw new IllegalStateException(); }
    public TypedValue getDefaultTypedValue() { throw new IllegalStateException(); }
    
    public final void check(StatementValue arg) throws CompilerException
    {
        CompleteType argType = arg.getCompleteType();
        ParameterType parType = getType();
        
        if(!parType.isCompatible(argType))
            throw new CompilerException("Cannot assign %s argument type in %s parameter type.", argType, parType);
    }
    
    private String getDefaultValueAsString()
    {
        if(isInteger())
            return getDefaultIntegerValue().toString();
        else if(isInternal())
            return getDefaultInternalValue().getInternalName();
        else if(isTypedValue())
            return getDefaultTypedValue().getName();
        else throw new IllegalStateException();
    }

    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getType()).append(' ').append(name);
        if(hasDefaultValue())
            sb.append(" = ").append(getDefaultValueAsString());

        return sb.toString();
    }
    
    private static CompleteType safeCompleteType(TypeModifier modifier, TypeId type)
    {
        try { return type.complete(modifier); }
        catch(CompilerException ex) { throw new IllegalStateException(); }
    }
    
    public static final Parameter integer(String name, Int32 defaultValue)
    {
        return new IntParameter(name, false, false, defaultValue);
    }
    public static final Parameter integer(String name) { return integer(name, null); }
    
    public static final Parameter constant(String name, Int32 defaultValue)
    {
        return new IntParameter(name, true, true, defaultValue);
    }
    public static final Parameter constant(String name) { return constant(name, null); }
    
    public static final Parameter variable(String name)
    {
        return new IntParameter(name, true, false, null);
    }
    
    
    public static final Parameter internal(String name, ScriptInternal defaultValue)
    {
        return new InternalParameter(name, defaultValue);
    }
    public static final Parameter internal(String name) { return internal(name, null); }
    
    public static final Parameter typed(String name, TypeId type, TypedValue defaultValue)
    {
        return new TypedValueParameter(name, type, defaultValue);
    }
    public static final Parameter typed(String name, TypeId type) { return typed(name, type, null); }
    
    
    
    
    
    private static final class IntParameter extends Parameter
    {
        private final Int32 defaultValue;
        private final ParameterType type;
        
        private IntParameter(String name, boolean modified, boolean isConst, Int32 defaultValue)
        {
            super(name);
            this.defaultValue = defaultValue;
            this.type = !modified
                    ? ParameterType.of(TypeId.INT)
                    : ParameterType.of(safeCompleteType(isConst ? TypeModifier.CONST : TypeModifier.VAR, TypeId.INT));
        }

        @Override
        public final ParameterType getType() { return type; }

        @Override
        public final boolean hasDefaultValue() { return defaultValue != null; }

        @Override
        public final Int32 getDefaultIntegerValue() { return defaultValue; }
        
        @Override
        public final StatementValue checkOrGetDefault(StatementValue value) throws CompilerException
        {
            if(value == null)
            {
                if(defaultValue == null)
                    throw new CompilerException("Parameter '%s' has not default value.", getName());
                return StatementValue.of(defaultValue);
            }
            
            check(value);
            return value;
        }
        
        @Override
        public final StatementValue compile(CompilerState state, CodeManager code, StatementValue value) throws CompilerException
        {
            if(value == null)
            {
                if(defaultValue == null)
                    throw new CompilerException("Parameter '%s' has not default value.", getName());
                
                StatementValue def = StatementValue.of(defaultValue);
                MemoryAddress.of(def).compileRead(state, code);
                return def;
            }
            
            check(value);
            value.toMemoryAddress().compileRead(state, code);
            return value;
        }
        
        @Override
        public final Int32 constCompile(Int32 value) throws CompilerException
        {
            if(value == null)
                return defaultValue;
            
            check(StatementValue.of(value));
            return value;
        }
    }
    
    private static final class InternalParameter extends Parameter
    {
        private final ScriptInternal defaultValue;
        private final ParameterType type;
        
        private InternalParameter(String name, ScriptInternal defaultValue)
        {
            super(name);
            this.defaultValue = defaultValue;
            this.type = ParameterType.of(safeCompleteType(TypeModifier.INTERNAL, TypeId.INT));
        }

        @Override
        public final ParameterType getType() { return type; }

        @Override
        public final boolean hasDefaultValue() { return defaultValue != null; }

        @Override
        public final ScriptInternal getDefaultInternalValue() { return defaultValue; }
        
        @Override
        public final StatementValue checkOrGetDefault(StatementValue value) throws CompilerException
        {
            if(value == null)
            {
                if(defaultValue == null)
                    throw new CompilerException("Parameter '%s' has not default value.", getName());
                return StatementValue.of(Namespace.getGlobalByInternal(defaultValue));
            }
            
            check(value);
            return value;
        }
        
        @Override
        public final StatementValue compile(CompilerState state, CodeManager code, StatementValue value) throws CompilerException
        {
            if(value == null)
            {
                if(defaultValue == null)
                    throw new CompilerException("Parameter '%s' has not default value.", getName());
                
                StatementValue def = StatementValue.of(Namespace.getGlobalByInternal(defaultValue));
                MemoryAddress.of(def).compileRead(state, code);
                return def;
            }
            
            check(value);
            value.toMemoryAddress().compileRead(state, code);
            return value;
        }
        
        @Override
        public final Int32 constCompile(Int32 value) throws CompilerException
        {
            throw new CompilerException("Invalid %s is not valid for const environment.", this);
        }
    }
    
    private static final class TypedValueParameter extends Parameter
    {
        private final TypedValue defaultValue;
        private final ParameterType type;
        
        private TypedValueParameter(String name, TypeId type, TypedValue defaultValue)
        {
            super(name);
            this.defaultValue = defaultValue;
            this.type = ParameterType.of(safeCompleteType(TypeModifier.INTERNAL, type));
            
            if(defaultValue != null && type != defaultValue.getType())
                throw new IllegalStateException();
            
            if(type == TypeId.INT)
                throw new IllegalStateException();
        }

        @Override
        public final ParameterType getType() { return type; }

        @Override
        public final boolean hasDefaultValue() { return defaultValue != null; }

        @Override
        public final TypedValue getDefaultTypedValue() { return defaultValue; }
        
        @Override
        public final StatementValue checkOrGetDefault(StatementValue value) throws CompilerException
        {
            if(value == null)
            {
                if(defaultValue == null)
                    throw new CompilerException("Parameter '%s' has not default value.", getName());
                return StatementValue.of(Namespace.getGlobalByToken(defaultValue.getToken()));
            }
            
            check(value);
            return value;
        }
        
        @Override
        public final StatementValue compile(CompilerState state, CodeManager code, StatementValue value) throws CompilerException
        {
            if(value == null)
            {
                if(defaultValue == null)
                    throw new CompilerException("Parameter '%s' has not default value.", getName());
                
                StatementValue def = StatementValue.of(Namespace.getGlobalByToken(defaultValue.getToken()));
                MemoryAddress.of(def).compileRead(state, code);
                return def;
            }
            
            check(value);
            code.insertTokenCode(value.getTypedValue().getToken());
            return value;
        }
        
        @Override
        public final Int32 constCompile(Int32 value) throws CompilerException
        {
            throw new CompilerException("Invalid %s is not valid for const environment.", this);
        }
    }
}
