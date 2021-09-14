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
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementTask.ConditionalState;
import kp.ps.script.parser.ArgumentList;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Command;
import kp.ps.script.parser.CommandId;
import kp.ps.script.parser.Fragment;
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.Statement;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class ConditionalInstruction extends Instruction
{
    private final Statement condition;
    private final Fragment action;
    private Fragment elseAction;
    
    private ConditionalInstruction(Statement condition, Fragment action)
    {
        this.condition = Objects.requireNonNull(condition);
        this.action = Objects.requireNonNull(action);
    }
    
    public final void insertElsePart(Fragment scope) throws CompilerException
    {
        if(elseAction != null)
            throw  new CompilerException("Cannot attach else statement after another else statement.");
        this.elseAction = Objects.requireNonNull(scope);
    }
    
    @Override
    public void normalCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        StatementTask condTask = StatementCompiler.toTask(state, condition);
        
        ConditionalState result = StatementCompiler.compileIfCommand(state, code, condTask);
        if(result != ConditionalState.UNKNOWN)
        {
            if(result == ConditionalState.TRUE)
                StatementCompiler.compileScope(state, code, action, true);
            else if(elseAction != null)
                StatementCompiler.compileScope(state, code, elseAction, true);
        }
        else
        {
            StatementCompiler.compileScope(state, code, action);
            if(elseAction != null)
                StatementCompiler.compileScope(state, code, elseAction);
        }
    }

    @Override
    public void constCompile(CompilerState state) throws CompilerException
    {
        StatementTask condTask = StatementCompiler.toTask(state, condition);
        
        if(condTask.constCompile().getConstantValue().toInt() != 0)
            StatementCompiler.compileConstScope(state, action);
        else if(elseAction != null)
            StatementCompiler.compileConstScope(state, elseAction);
    }

    @Override
    public void staticCompile(CompilerState state, CodeManager initCode, CodeManager mainCode) throws CompilerException
    {
        throw new CompilerException("Conditional elements (if, else) cannot work in static environment (out of any code section).");
    }
    
    public static final ConditionalInstruction parse(
            CodeReader reader,
            Instruction lastInstruction,
            boolean isElse,
            ErrorList errors) throws CompilerException
    {
        if(isElse)
        {
            parseElse(reader, lastInstruction, errors);
            return null;
        }
        return parseIf(reader, errors);
    }
    
    private static ConditionalInstruction parseIf(CodeReader reader, ErrorList errors) throws CompilerException
    {
        CodeParser parser = new CodeParser();
        FragmentList frags = parser.parseCommandArgsAndScope(reader, Command.fromId(CommandId.IF), errors);
        
        ArgumentList args = (ArgumentList) frags.get(0);
        Fragment scope = frags.get(1);
        
        if(args.size() != 1 || !args.isCallArguments())
            throw new CompilerException("Expected valid statement inside 'if' parentheis. 'if(<statement>)...'.");
        
        return new ConditionalInstruction(args.getArgument(0).getStatement(), scope);
    }
    
    private static void parseElse(CodeReader reader, Instruction lastInstruction, ErrorList errors) throws CompilerException
    {
        if(lastInstruction == null || !(lastInstruction instanceof ConditionalInstruction))
            throw new CompilerException("Can only put 'else' command after 'if' command.");
        
        CodeParser parser = new CodeParser();
        FragmentList frags = parser.parseCommandScope(reader, Command.fromId(CommandId.ELSE), errors);
        
        ConditionalInstruction cond = (ConditionalInstruction) lastInstruction;
        cond.insertElsePart(frags.get(0));
    }
}
