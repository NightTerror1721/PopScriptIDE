/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.util.Objects;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.parser.Statement;

/**
 *
 * @author Marc
 */
public class ConditionalInstruction extends Instruction
{
    private final Statement condition;
    private final Statement action;
    private Statement elseAction;
    
    private ConditionalInstruction(Statement condition, Statement action)
    {
        this.condition = Objects.requireNonNull(condition);
        this.action = Objects.requireNonNull(action);
    }
    
    @Override
    public void compile(CompilerState state)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
