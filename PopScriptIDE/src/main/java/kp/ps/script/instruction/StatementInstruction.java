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
import kp.ps.script.compiler.statement.StatementCompiler;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Fragment;
import kp.ps.script.parser.Statement;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class StatementInstruction extends Instruction
{
    private final Statement statement;
    
    private StatementInstruction(int firstLine, int lastLine, Statement statement)
    {
        super(firstLine, lastLine);
        this.statement = Objects.requireNonNull(statement);
    }
    
    @Override
    public void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        StatementCompiler.toTask(state, statement).normalCompile(state, code);
    }

    @Override
    public void constCompile(CompilerState state) throws CompilerException
    {
        StatementCompiler.toTask(state, statement).constCompile();
    }
    
    @Override
    public void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        throw new CompilerException("Regular statements cannot work in static environment (out of any code section).");
    }
    
    @Override
    public final boolean hasYieldInstruction() { return false; }
    
    public static final StatementInstruction parse(CodeReader reader, CodeParser parser, ErrorList errors, Fragment... preFragments) throws CompilerException
    {
        int first = reader.getCurrentLine();
        Statement statement = parser.parseInlineInstruction(reader, errors, preFragments);
        int last = reader.getCurrentLine();
        return new StatementInstruction(first, last, statement);
    }
}
