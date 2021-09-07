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
public final class MulDivCompilation implements StatementTask
{
    private final StatementTask leftOperand;
    private final StatementTask rightOperand;
    private final boolean mulMode;
    private final boolean compoundAssignment;
    
    MulDivCompilation(StatementTask leftOperand, StatementTask rightOperand, boolean mulMode, boolean compoundAssignment)
    {
        this.leftOperand = Objects.requireNonNull(leftOperand);
        this.rightOperand = Objects.requireNonNull(rightOperand);
        this.mulMode = mulMode;
        this.compoundAssignment = compoundAssignment;
    }
    
    @Override
    public MemoryAddress normalCompile(CompilerState state, CodeManager code, MemoryAddress retloc) throws CompilerException
    {
        if(compoundAssignment)
        {
            try(TemporaryVars temps = TemporaryVars.open(state, code))
            {
                MemoryAddress left = temps.normalCompileWithTemp(leftOperand);
                MemoryAddress right = temps.normalCompileWithTemp(rightOperand);

                code.insertTokenCode(mulMode ? ScriptToken.MULTIPLY : ScriptToken.DIVIDE);
                left.compileWrite(state, code);
                left.compileRead(state, code);
                right.compileRead(state, code);
                
                if(!retloc.isInvalid())
                    return StatementTaskUtils.assignation(retloc, left).normalCompile(state, code);
                return retloc;
            }
        }
        else
        {
            if(retloc.isInvalid())
                throw new CompilerException("The %s result must be stored in any valid location.", (mulMode ? "mul" : "div"));

            try(TemporaryVars temps = TemporaryVars.open(state, code))
            {
                MemoryAddress left = temps.normalCompileWithTemp(leftOperand);
                MemoryAddress right = temps.normalCompileWithTemp(rightOperand);

                if(left.isConstant() && right.isConstant())
                    return MemoryAddress.of(constOperation(left.getConstantValue(), right.getConstantValue()));

                code.insertTokenCode(mulMode ? ScriptToken.MULTIPLY : ScriptToken.DIVIDE);
                retloc.compileWrite(state, code);
                left.compileRead(state, code);
                right.compileRead(state, code);
                return retloc;
            }
        }
    }
    
    @Override
    public final MemoryAddress varCompile(CompilerState state, CodeManager code) throws CompilerException
    {
        if(compoundAssignment)
            throw new CompilerException("Cannot use %s operator in var assignment.", (mulMode ? "*=" : "/="));
        throw new CompilerException("Cannot use %s operator in var assignment.", (mulMode ? "*" : "/"));
    }

    @Override
    public StatementValue constCompile() throws CompilerException
    {
        StatementValue left = leftOperand.constCompile();
        StatementValue right = rightOperand.constCompile();
        
        if(compoundAssignment)
            throw new CompilerException("Cannot use %s operator in internal environment.", (mulMode ? "*=" : "/="));
        
        return StatementValue.of(constOperation(left.getConstantValue().toInt(), right.getConstantValue().toInt()));
    }
    
    @Override
    public final StatementValue internalCompile() throws CompilerException
    {
        throw new CompilerException("Cannot use %s operator in internal environment.", (mulMode ? "*" : "/"));
    }

    @Override
    public ConditionalState conditionalCompile(CompilerState state, CodeManager prev, CodeManager cond) throws CompilerException
    {
        try(TemporaryVars temps = TemporaryVars.open(state, prev))
        {
            return temps.normalCompileWithTemp(this).conditionalCompile(state, prev, cond);
        }
    }
    
    private int constOperation(int left, int right)
    {
        return mulMode 
                ? left * right
                : left / right;
    }
}
