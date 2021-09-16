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
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Command;
import kp.ps.script.parser.CommandId;
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.Scope;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class MainInitInstruction extends Instruction
{
    private final Instruction[] instructions;
    private final boolean isMain;
    
    private MainInitInstruction(int firstLine, int lastLine, Instruction[] instructions, boolean isMain)
    {
        super(firstLine, lastLine);
        
        this.instructions = Objects.requireNonNull(instructions);
        this.isMain = isMain;
    }
    
    @Override
    public void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use '%s' command in non static environment.", (isMain ? "main" : "init"));
    }

    @Override
    public void constCompile(CompilerState state) throws CompilerException
    {
        throw new CompilerException("Cannot use '%s' command in non static environment.", (isMain ? "main" : "init"));
    }

    @Override
    public void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        if(state.hasLocalElements())
            throw new IllegalStateException();
        
        CodeManager code = new CodeManager();
        state.pushLocalElements();
        for(Instruction inst : instructions)
            inst.normalCompile(state, code);
        state.popLocalElements();
        
        if(isMain)
            mainCode.insertCode(code);
        else initCode.insertCode(code);
    }
    
    @Override
    public final boolean hasYieldInstruction() { return false; }
    
    public static final MainInitInstruction parse(CodeReader reader, CodeParser parser, boolean isMain, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        FragmentList list = parser.parseCommandScope(reader, Command.fromId(isMain ? CommandId.MAIN : CommandId.INIT), errors);
        int last = reader.getCurrentLine();
        
        Scope scope = list.get(0);
        return new MainInitInstruction(first, last, scope.getInstructions(), isMain);
    }
}
