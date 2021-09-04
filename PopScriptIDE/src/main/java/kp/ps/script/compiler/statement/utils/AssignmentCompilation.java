/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement.utils;

import java.util.Objects;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;

/**
 *
 * @author Marc
 */
public class AssignmentCompilation implements StatementTask
{
    private final StatementTask target;
    private final StatementTask value;
    
    AssignmentCompilation(StatementTask target, StatementTask value)
    {
        this.target = Objects.requireNonNull(target);
        this.value = Objects.requireNonNull(value);
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        try(TemporaryVars temps = TemporaryVars.open(state, code))
        {
            MemoryAddress left = target.normalCompile(state, code);
            MemoryAddress right = temps.normalCompileWithTemp(value);

            if(left.canWrite())
                throw new CompilerException("Required valid variable or non constant internal to store any value in normal environment.");

            code.insertTokenCode(ScriptToken.SET);
            left.compileWrite(state, code);
            right.compileRead(state, code);

            if(!retloc.isInvalid())
            {
                code.insertTokenCode(ScriptToken.SET);
                retloc.compileWrite(state, code);
                left.compileRead(state, code);
                return retloc;
            }
            else return left;
        }
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        StatementValue left = target.constCompile();
        StatementValue right = value.constCompile();
        
        if(!left.isConstant() || !right.isConstant())
            throw new IllegalStateException();
        
        left.initiateConstant(right.getConstantValue());
        return left;
    }
    
    @Override
    public final StatementValue internalCompile() throws CompilerException
    {
        StatementValue left = target.internalCompile();
        StatementValue right = value.internalCompile();
        
        if(left.getType() != right.getType())
            throw new CompilerException("Cannot assign %s type to %s.", left.getType(), right.getType());
        
        if(left.isInternal())
            left.initiateInternal(right.getInternal());
        else if(left.isTypedValue())
            left.initiateTypedValue(right.getTypedValue());
        else throw new IllegalStateException();
        
        return left;
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException
    {
        return normalCompile(state, prev).conditionalCompile(state, prev, cond);
    }
    
}
