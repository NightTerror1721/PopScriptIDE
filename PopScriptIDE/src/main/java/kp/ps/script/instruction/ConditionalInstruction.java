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
import kp.ps.script.compiler.statement.StatementCompiler;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementTask.ConditionalState;
import kp.ps.script.parser.Fragment;
import kp.ps.script.parser.Statement;

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
}
