/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.util.Objects;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.namespace.Namespace;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Command;
import kp.ps.script.parser.CommandId;
import kp.ps.script.parser.ElementReference;
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.NamespaceResolver;
import kp.ps.script.parser.Scope;
import kp.ps.script.parser.Statement;
import kp.ps.script.parser.StatementParser;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class NamespaceInstruction extends Instruction
{
    private final ElementReference ref;
    private final Instruction[] instructions;
    
    private NamespaceInstruction(ElementReference ref, Instruction[] instructions)
    {
        this.ref = Objects.requireNonNull(ref);
        this.instructions = Objects.requireNonNull(instructions);
    }
    
    @Override
    public void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        constCompile(state);
    }

    @Override
    public void constCompile(CompilerState state) throws CompilerException
    {
        Namespace old = pushNamespace(state);
        for(Instruction inst : instructions)
            inst.constCompile(state);
        popNamespace(state, old);
    }

    @Override
    public void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        Namespace old = pushNamespace(state);
        for(Instruction inst : instructions)
            inst.staticCompile(state, initCode, mainCode);
        popNamespace(state, old);
    }
    
    private Namespace pushNamespace(CompilerState state)
    {
        Namespace old = state.getNamespace();
        if(ref.isIdentifier())
            state.pushNamespace(ref.toString());
        else
        {
            NamespaceResolver resolver = ref.getNamespaceResolver();
            int len = resolver.size();
            for(int i = 0; i < len; ++i)
                state.pushNamespace(resolver.getIdentifier(i).toString());
        }
        return old;
    }
    
    private void popNamespace(CompilerState state, Namespace old)
    {
        do
        {
            state.popNamespace();
        }
        while(old != state.getNamespace());
    }
    
    public static final NamespaceInstruction parse(CodeReader reader, ErrorList errors) throws CompilerException
    {
        CodeParser parser = new CodeParser();
        FragmentList list = parser.parseUntilScopeAsList(reader, Command.fromId(CommandId.NAMESPACE), errors);
        
        int len = list.size();
        if(len < 2)
            throw new CompilerException("Expected valid identifier after 'namespace' command.");
        
        Scope scope = list.get(len - 1);
        Statement statement = StatementParser.parse(list.subList(0, len - 1));
        if(!statement.isElementReference())
            throw new CompilerException("Expected valid identifier after 'namespace' command.");
        
        return new NamespaceInstruction((ElementReference) statement, scope.getInstructions());
    }
}
