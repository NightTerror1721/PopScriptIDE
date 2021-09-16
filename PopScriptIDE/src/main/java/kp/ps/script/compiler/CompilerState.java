/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import kp.ps.script.Script;
import kp.ps.script.compiler.functions.Macro;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.namespace.Namespace;
import kp.ps.utils.Pair;

/**
 *
 * @author Marc
 */
public final class CompilerState
{
    private final FieldsManager fieldsManager = new FieldsManager();
    private LocalElementsScope localElements = null;
    private Namespace namespace = Namespace.createRoot();
    private final HashSet<Macro> invokedMacros = new HashSet<>();
    private final LinkedList<Pair<Macro, MemoryAddress>> invokedMacrosStack = new LinkedList<>();
    private final ErrorList errors;
    
    
    public CompilerState(ErrorList errors) { this.errors = Objects.requireNonNull(errors); }
    
    public final FieldsManager getFields() { return fieldsManager; }
    public final LocalElementsScope getLocalElements() { return localElements; }
    public final Namespace getNamespace() { return namespace; }
    public final ErrorList getErrors() { return errors; }
    
    public final boolean hasLocalElements() { return localElements != null; }
    
    public final LocalElementsScope pushLocalElements()
    {
        return localElements = localElements == null
                ? new LocalElementsScope(fieldsManager)
                : localElements.createChild();
    }
    
    public final LocalElementsScope popLocalElements()
    {
        if(localElements == null)
            throw new IllegalStateException();
        
        LocalElementsScope old = localElements;
        localElements = localElements.getParent();
        old.clear();
        return old;
    }
    
    public final Namespace pushNamespace(String name) throws CompilerException
    {
        Namespace child;
        if(!namespace.existsChild(name))
            child = namespace.createChild(name);
        else child = namespace.getChild(name);
        if(child == null)
            throw new IllegalStateException();
        namespace = child;
        return child;
    }
    
    public final Namespace popNamespace()
    {
        Namespace old = namespace;
        namespace = namespace.getParent();
        if(namespace == null)
            throw new IllegalStateException();
        return old;
    }
    
    public final void pushMacroInvocation(Macro macro, MemoryAddress yieldloc) throws CompilerException
    {
        if(invokedMacros.contains(macro))
            throw new CompilerException("Macro '%s' already invoked. Recursion is illegal.", macro.getName());
        
        invokedMacros.add(macro);
        invokedMacrosStack.push(Pair.of(macro, yieldloc));
    }
    
    public final void popMacroInvocation()
    {
        if(invokedMacrosStack.isEmpty())
            throw new IllegalStateException();
        
        Pair<Macro, MemoryAddress> pair = invokedMacrosStack.pop();
        invokedMacros.remove(pair.left);
    }
    
    public final Macro getCurrentInvokedMacro()
    {
        if(invokedMacrosStack.isEmpty())
            throw new IllegalStateException();
        return invokedMacrosStack.peek().left;
    }
    
    public final MemoryAddress getCurrentInvokedMacroYield()
    {
        if(invokedMacrosStack.isEmpty())
            throw new IllegalStateException();
        return invokedMacrosStack.peek().right;
    }
    
    public final boolean isOnInvocation() { return !invokedMacros.isEmpty(); }
    
    
    public final Script compileScript(CodeManager code)
    {
        Script script = new Script();
        code.insertToScript(script);
        fieldsManager.insertToScript(script);
        return script;
    }
    
    final void clear()
    {
        while(localElements != null)
            popLocalElements();
        while(!namespace.isGlobal())
            popNamespace().deepClear();
        fieldsManager.clear();
        invokedMacros.clear();
    }
}
