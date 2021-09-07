/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement.utils;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.Objects;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.LocalElementsScope.Element;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;

/**
 *
 * @author Marc
 */
public final class TemporaryVars implements Closeable
{
    private final CompilerState state;
    private final CodeManager code;
    private final LinkedList<Element> vars = new LinkedList<>();
    
    private TemporaryVars(CompilerState state, CodeManager code)
    {
        this.state = Objects.requireNonNull(state);
        this.code = Objects.requireNonNull(code);
    }
    
    private static MemoryAddress addr(Element elem)
    {
        try { return MemoryAddress.of(elem); }
        catch(CompilerException ex) { throw new IllegalStateException(); }
    }
    
    public final MemoryAddress push() throws CompilerException
    {
        Element elem = state.getLocalElements().pushTemporal();
        vars.add(elem);
        return addr(elem);
    }
    
    public final MemoryAddress peek()
    {
        if(vars.isEmpty())
            throw new IllegalStateException();
        return addr(vars.peek());
    }
    
    public final void pop()
    {
        if(vars.isEmpty())
            throw new IllegalStateException();
        vars.pop();
        state.getLocalElements().popTemporal();
    }
    
    public final void popIfNotUsed()
    {
        if(vars.isEmpty())
            throw new IllegalStateException();
        Element elem = vars.peek();
        if(!elem.isVariableInitiated())
        {
            vars.pop();
            state.getLocalElements().popTemporal();
        }
    }
    
    public final void clear()
    {
        if(!vars.isEmpty())
        {
            while(!vars.isEmpty())
            {
                if(!vars.peek().isVariableInitiated())
                    state.getLocalElements().popTemporal();
                vars.pop();
            }
        }
    }
    
    public final MemoryAddress normalCompileWithTemp(StatementTask task) throws CompilerException
    {
        MemoryAddress addr = push();
        addr = task.normalCompile(state, code, addr);
        popIfNotUsed();
        return addr;
    }
    
    public final StatementValue argCompileWithTemp(StatementTask task) throws CompilerException
    {
        StatementValue value = task.argCompile(state, code, push());
        popIfNotUsed();
        return value;
    }

    @Override
    public final void close()
    {
        clear();
    }
    
    public static final TemporaryVars open(CompilerState state, CodeManager code) { return new TemporaryVars(state, code); }
}
