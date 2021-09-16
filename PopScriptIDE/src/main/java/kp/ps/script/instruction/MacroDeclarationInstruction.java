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
import kp.ps.script.compiler.functions.Macro;
import kp.ps.script.parser.CodeParser;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class MacroDeclarationInstruction extends Instruction
{
    private final Macro macro;
    
    private MacroDeclarationInstruction(int firstLine, int lastLine, Macro macro)
    {
        super(firstLine, lastLine);
        this.macro = Objects.requireNonNull(macro);
    }
    
    @Override
    void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        throw new CompilerException("Cannot use 'macro' command in non static environment.");
    }

    @Override
    void constCompile(CompilerState state) throws CompilerException
    {
        throw new CompilerException("Cannot use 'macro' command in non static environment.");
    }

    @Override
    void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        state.getNamespace().addMacro(macro);
    }

    @Override
    public boolean hasYieldInstruction() { return false; }
    
    public static final MacroDeclarationInstruction parse(CompilerState state, CodeReader reader, CodeParser parser, ErrorList errors)
            throws CompilerException
    {
        int first = reader.getCurrentLine();
        Macro macro = Macro.parse(state, reader, parser, errors);
        int last = reader.getCurrentLine();
        return new MacroDeclarationInstruction(first, last, macro);
    }
    
}
