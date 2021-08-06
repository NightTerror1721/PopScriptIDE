/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.Objects;
import kp.ps.script.Script;
import kp.ps.script.namespace.Namespace;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public final class CompilerState
{
    private final FieldsManager fieldsManager = new FieldsManager();
    private final ErrorList errors = new ErrorList();
    private final CodeReader codeInput;
    private LocalElementsScope localElements = new LocalElementsScope(fieldsManager);
    private Namespace namespace = Namespace.createRoot();
    
    public CompilerState(CodeReader codeInput)
    {
        this.codeInput = Objects.requireNonNull(codeInput);
    }
    
    
    public final FieldsManager getFields() { return fieldsManager; }
    public final LocalElementsScope getLocalElements() { return localElements; }
    public final Namespace getNamespace() { return namespace; }
    
    public final LocalElementsScope pushLocalElements()
    {
        return localElements = localElements.createChild();
    }
    
    public final LocalElementsScope popLocalElements()
    {
        LocalElementsScope old = localElements;
        localElements = localElements.getParent();
        if(localElements == null)
            throw new IllegalStateException();
        return old;
    }
    
    public final Namespace pushNamespace(String name)
    {
        Namespace child;
        if(!namespace.existsChild(name))
            child = namespace.createChild(name);
        else child = namespace.getChild(name);
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
    
    
    public final Script compileScript(CodeManager code)
    {
        Script script = new Script();
        code.insertToScript(script);
        fieldsManager.insertToScript(script);
        return script;
    }
}
