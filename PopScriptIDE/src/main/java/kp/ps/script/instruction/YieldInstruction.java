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
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementCompiler;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.utils.StatementTaskUtils;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Statement;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class YieldInstruction extends Instruction
{
    private final Statement statement;
    
    private YieldInstruction(int firstLine, int lastLine, Statement statement)
    {
        super(firstLine, lastLine);
        this.statement = Objects.requireNonNull(statement);
    }
    
    @Override
    void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        if(!state.isOnInvocation())
            throw new CompilerException("Invalid use of 'yield' command. Can only use it into macro's code.");
        
        MemoryAddress yieldloc = state.getCurrentInvokedMacroYield();
        StatementTask task = StatementCompiler.toTask(state, statement);
        StatementTaskUtils.assignation(yieldloc, task).normalCompile(state, code);
    }

    @Override
    void constCompile(CompilerState state) throws CompilerException
    {
        throw new CompilerException("Cannot use 'yield' command in const environment.");
    }

    @Override
    void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        throw new CompilerException("Cannot use 'yield' command in static environment.");
    }

    @Override
    public boolean hasYieldInstruction() { return true; }
    
    public static final YieldInstruction parse(CodeReader reader, CodeParser parser, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        Statement statement = parser.parseInlineInstruction(reader, errors);
        int last = reader.getCurrentLine();
        return new YieldInstruction(first, last, statement);
    }
}
