/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement;

import java.util.Objects;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.LocalElementsScope.Element;
import kp.ps.script.namespace.NamespaceField;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public abstract class MemoryAddress implements StatementTask
{
    private MemoryAddress() {}
    
    public abstract boolean canRead() throws CompilerException;
    public abstract boolean canWrite() throws CompilerException;
    public abstract void compileRead(CompilerState state, CodeManager code) throws CompilerException;
    
    public abstract boolean isConstant();
    public abstract int getConstantValue() throws CompilerException;
    
    public abstract StatementValue toStatementValue();
    
    @Override
    public abstract boolean equals(Object o);
    
    @Override
    public abstract int hashCode();
    
    public final void compileWrite(CompilerState state, CodeManager code) throws CompilerException
    {
        if(!canWrite())
            throw new CompilerException("Cannot store in constant location.");
        compileRead(state, code);
    }
    
    public abstract boolean isInvalid();
    
    public static final MemoryAddress of(StatementValue value) throws CompilerException { return new ValueMemoryAddress(value); }
    public static final MemoryAddress of(int value) throws CompilerException { return of(StatementValue.of(value)); }
    public static final MemoryAddress of(Int32 value) throws CompilerException { return of(StatementValue.of(value)); }
    public static final MemoryAddress of(Element element) throws CompilerException { return of(StatementValue.of(element)); }
    public static final MemoryAddress of(NamespaceField field) throws CompilerException { return of(StatementValue.of(field)); }
    
    public static final MemoryAddress invalid() { return INVALID; }
    
    
    private static final class ValueMemoryAddress extends MemoryAddress
    {
        private final StatementValue value;
        
        private ValueMemoryAddress(StatementValue value) throws CompilerException
        {
            this.value = Objects.requireNonNull(value);
            if(value.isTypedValue())
                throw new CompilerException("Typed value is not a valid store location.");
        }
        
        @Override
        public final boolean isInvalid() { return false; }
        
        @Override
        public final boolean canRead() { return true; }

        @Override
        public final boolean canWrite() throws CompilerException
        {
            return value.isVariable() || (value.isInternal() && !value.getInternal().isConstant());
        }

        @Override
        public final void compileRead(CompilerState state, CodeManager code) throws CompilerException
        {
            if(value.isConstant())
                code.insertFieldCode(state.getFields().registerConstant(value.getConstantValue()));
            else if(value.isVariable())
                code.insertFieldCode(state.getFields().getVariableFieldLocation(value.getVariableIndex(true)));
            else if(value.isInternal())
                code.insertFieldCode(state.getFields().registerInternal(value.getInternal()));
        }

        @Override
        public final boolean isConstant() { return value.isConstant(); }

        @Override
        public final int getConstantValue() throws CompilerException { return value.getConstantValue().toInt(); }
        
        @Override
        public final StatementValue toStatementValue() { return value; }
        
        @Override
        public final boolean equals(Object o)
        {
            if(this == o)
                return true;
            if(o == null)
                return false;
            if(o instanceof ValueMemoryAddress)
                return value.equals(((ValueMemoryAddress) o).value);
            return false;
        }

        @Override
        public final int hashCode()
        {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public MemoryAddress normalCompile(CompilerState state, CodeManager code) throws CompilerException
        {
            return value.normalCompile(state, code);
        }
        
        @Override
        public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
        {
            return value.normalCompile(state, code, retloc);
        }
        
        @Override
        public final MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
        {
            return value.varCompile(state, code);
        }

        @Override
        public StatementValue constCompile() throws CompilerException
        {
            return value.constCompile();
        }
        
        @Override
        public final StatementValue internalCompile() throws CompilerException
        {
            return value.internalCompile();
        }

        @Override
        public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException
        {
            return value.conditionalCompile(state, prev, cond);
        }
    }
    
    private static final class InvalidMemoryAddress extends MemoryAddress
    {
        private InvalidMemoryAddress() {}
        
        @Override
        public final boolean isInvalid() { return true; }
        
        @Override
        public final boolean canRead() { return false; }

        @Override
        public final boolean canWrite() { return false; }

        @Override
        public final void compileRead(CompilerState state, CodeManager code) throws CompilerException
        {
            throw new IllegalStateException();
        }
        
        @Override
        public final boolean isConstant() { return false; }

        @Override
        public final int getConstantValue() { throw new IllegalStateException(); }
        
        @Override
        public final StatementValue toStatementValue() { throw new IllegalStateException(); }
        
        @Override
        public final boolean equals(Object o)
        {
            if(this == o)
                return true;
            if(o == null)
                return false;
            return o instanceof InvalidMemoryAddress;
        }
        
        @Override
        public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
        {
            throw new IllegalStateException();
        }
        
        @Override
        public final MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
        {
            throw new IllegalStateException();
        }

        @Override
        public StatementValue constCompile() throws CompilerException
        {
            throw new IllegalStateException();
        }
        
        @Override
        public final StatementValue internalCompile() throws CompilerException
        {
            throw new IllegalStateException();
        }

        @Override
        public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException
        {
            throw new IllegalStateException();
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            return hash;
        }
    }
    private static final InvalidMemoryAddress INVALID = new InvalidMemoryAddress();
}
