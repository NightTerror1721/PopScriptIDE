/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.util.Objects;
import kp.ps.script.ScriptToken;
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
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.Scope;
import kp.ps.script.parser.Statement;
import kp.ps.utils.CodeReader;

/**
 *
 * @author Marc
 */
public class ConditionalInstruction extends Instruction
{
    private final Statement condition;
    private final Scope action;
    private Scope elseAction;
    
    private ConditionalInstruction(int firstLine, int lastLine, Statement condition, Scope action)
    {
        super(firstLine, lastLine);
        this.condition = Objects.requireNonNull(condition);
        this.action = Objects.requireNonNull(action);
    }
    
    public final void insertElsePart(Scope scope) throws CompilerException
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
            {
                code.insertTokenCode(ScriptToken.ELSE);
                StatementCompiler.compileScope(state, code, elseAction);
            }
            code.insertTokenCode(ScriptToken.ENDIF);
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
    
    @Override
    public final boolean hasYieldInstruction()
    {
        for(Instruction inst : action.getInstructions())
            if(inst.hasYieldInstruction())
                return true;
        
        if(elseAction != null)
            for(Instruction inst : elseAction.getInstructions())
                if(inst.hasYieldInstruction())
                    return true;
        
        return false;
    }
    
    public static final ConditionalInstruction parse(
            CodeReader reader,
            CodeParser parser,
            Instruction lastInstruction,
            boolean isElse,
            ErrorList errors) throws CompilerException
    {
        if(isElse)
        {
            parseElse(reader, parser, lastInstruction, errors);
            return null;
        }
        return parseIf(reader, parser, errors);
    }
    
    private static ConditionalInstruction parseIf(CodeReader reader, CodeParser parser, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        FragmentList frags = parser.parseCommandArgsAndScope(reader, Command.fromId(CommandId.IF), errors);
        int last = reader.getCurrentLine();
        
        ArgumentList args = (ArgumentList) frags.get(0);
        Scope scope = frags.get(1);
        
        if(args.size() != 1 || !args.isCallArguments())
            throw new CompilerException("Expected valid statement inside 'if' parentheis. 'if(<statement>)...'.");
        
        return new ConditionalInstruction(first, last, args.getArgument(0).getStatement(), scope);
    }
    
    private static void parseElse(CodeReader reader, CodeParser parser, Instruction lastInstruction, ErrorList errors) throws CompilerException
    {
        if(lastInstruction == null || !(lastInstruction instanceof ConditionalInstruction))
            throw new CompilerException("Can only put 'else' command after 'if' command.");
        
        FragmentList frags = parser.parseCommandScope(reader, Command.fromId(CommandId.ELSE), errors);
        int last = reader.getCurrentLine();
        
        ConditionalInstruction cond = (ConditionalInstruction) lastInstruction;
        cond.insertElsePart(frags.get(0));
        cond.updateLastLine(last);
    }

    /*@Override
    String toString(int currentIdent, int deltaIdent)
    {
        StringBuilder sb = new StringBuilder();
        String oldIdent = Utils.stringDup(' ', currentIdent);
        String newIdent = Utils.stringDup(' ', currentIdent + deltaIdent);
        
        sb.append(oldIdent)
                .append(CommandId.IF)
                .append('(')
                .append(condition)
                .append(')')
                .append('\n');
        action.
    }*/
}
