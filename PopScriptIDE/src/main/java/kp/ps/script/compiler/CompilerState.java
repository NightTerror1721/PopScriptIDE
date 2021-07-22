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
    private final CodeManager codeManager = new CodeManager();
    private final Namespace namespace = Namespace.createRoot();
    private final ErrorList errors = new ErrorList();
    private final CodeReader codeInput;
    private LocalElementsScope localElements = new LocalElementsScope(fieldsManager);
    
    public CompilerState(CodeReader codeInput)
    {
        this.codeInput = Objects.requireNonNull(codeInput);
    }
    
    
    public final FieldsManager getFields() { return fieldsManager; }
    public final CodeManager getCode() { return codeManager; }
    public final LocalElementsScope getLocalElements() { return localElements; }
    public final Namespace getRootNamespace() { return namespace; }
    
    public final LocalElementsScope pushLocalElements()
    {
        return localElements = localElements.createChild();
    }
    
    public final LocalElementsScope popLocalElements()
    {
        localElements = localElements.getParent();
        if(localElements == null)
            throw new IllegalStateException();
        return localElements;
    }
    
    
    public final Script compileScript()
    {
        Script script = new Script();
        codeManager.insertToScript(script);
        fieldsManager.insertToScript(script);
        return script;
    }
}
