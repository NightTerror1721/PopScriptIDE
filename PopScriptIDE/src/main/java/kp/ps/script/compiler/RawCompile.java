/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;

/**
 *
 * @author Marc
 */
public final class RawCompile
{
    private RawCompile() {}
    
    public static final void compileStatementValue(CompilerState state, StatementValue value) throws CompilerException
    {
        switch(value.getKind())
        {
            case VARIABLE:
                state.getCode().insertFieldCode(state.getFields().getVariableFieldLocation(value.getVariableIndex(true)));
                break;
                
            case CONSTANT:
                state.getCode().insertFieldCode(state.getFields().registerConstant(value.getConstantValue()));
                break;
                
            case INTERNAL:
                state.getCode().insertFieldCode(state.getFields().registerInternal(value.getInternal()));
                break;
                
            case TOKEN:
                state.getCode().insertTokenCode(value.getToken());
                
            default:
                throw new IllegalStateException();
        }
    }
    
    public static final void compileStoringLocation(CompilerState state, StoringLocation loc) throws CompilerException
    {
        compileStatementValue(state, loc.toStatementValue());
    }
    
    private static void checkIsValidStoringLocation(StatementValue value) throws CompilerException
    {
        if(value.isConstant())
            throw new CompilerException("Cannot store anything into constant element.");
        if(value.isToken())
            throw new CompilerException("Cannot store anything into token constant element.");
    }
    
    public static final StatementValue compileSet(CompilerState state, StatementTask dst, StatementTask src, StoringLocation retloc)
            throws CompilerException
    {
        StatementValue left = dst.compile();
        checkIsValidStoringLocation(left);
        
        StatementValue right = src.compile();
        if(right.isToken())
            throw new CompilerException("Cannot store token constant into anything.");
        
        state.getCode().insertTokenCode(ScriptToken.SET);
        compileStatementValue(state, left);
        compileStatementValue(state, right);
        
        if(!retloc.isEmpty())
            compileSet(state, retloc.toStatementValue(), left, StoringLocation.empty());
        
        return left;
    }
    
    private static StatementValue compileSumSub(
            CompilerState state,
            StatementTask op1,
            StatementTask op2,
            StoringLocation retloc,
            boolean isSum
    ) throws CompilerException
    {
        StatementValue left = op1.compile();
        StatementValue right = op2.compile();
        
        if(left.getType() != right.getType())
            throw new CompilerException("Cannot %s '%s' with '%s'.", (isSum ? "sum" : "sub"), left.getType(), right.getType());
        
        if(left.getType() != TypeId.INT)
            throw new CompilerException("Cannot %s '%s'. Only int type is valid.", (isSum ? "sum" : "sub"), left.getType());
        
        if(retloc.isEmpty())
            throw new CompilerException("The %s result must be stored in any valid location.", (isSum ? "sum" : "sub"));
        
        if(left.isConstant() && right.isConstant())
        {
            if(isSum)
                return StatementValue.of(left.getConstantValue().toInt() + right.getConstantValue().toInt());
            return StatementValue.of(left.getConstantValue().toInt() - right.getConstantValue().toInt());
        }
        
        if(!left.equals(retloc.toStatementValue()))
            compileSet(state, retloc.toStatementValue(), right, StoringLocation.empty());
        state.getCode().insertTokenCode(isSum ? ScriptToken.INCREMENT : ScriptToken.DECREMENT);
        compileStatementValue(state, retloc.toStatementValue());
        compileStatementValue(state, right);
        return retloc.toStatementValue();
    }
    
    public static final StatementValue compileSum(CompilerState state, StatementTask op1, StatementTask op2, StoringLocation retloc)
            throws CompilerException
    {
        return compileSumSub(state, op1, op2, retloc, true);
    }
    
    public static final StatementValue compileSub(CompilerState state, StatementTask op1, StatementTask op2, StoringLocation retloc)
            throws CompilerException
    {
        return compileSumSub(state, op1, op2, retloc, false);
    }
    
    
    private static StatementValue compileMulDiv(
            CompilerState state,
            StatementTask op1,
            StatementTask op2,
            StoringLocation retloc,
            boolean isMul
    ) throws CompilerException
    {
        StatementValue left = op1.compile();
        StatementValue right = op2.compile();
        
        if(left.getType() != right.getType())
            throw new CompilerException("Cannot %s '%s' with '%s'.", (isMul ? "mul" : "div"), left.getType(), right.getType());
        
        if(left.getType() != TypeId.INT)
            throw new CompilerException("Cannot %s '%s'. Only int type is valid.", (isMul ? "mul" : "div"), left.getType());
        
        if(retloc.isEmpty())
            throw new CompilerException("The %s result must be stored in any valid location.", (isMul ? "mul" : "div"));
        
        if(left.isConstant() && right.isConstant())
        {
            if(isMul)
                return StatementValue.of(left.getConstantValue().toInt() * right.getConstantValue().toInt());
            return StatementValue.of(left.getConstantValue().toInt() / right.getConstantValue().toInt());
        }
        
        state.getCode().insertTokenCode(isMul ? ScriptToken.MULTIPLY : ScriptToken.DIVIDE);
        compileStatementValue(state, retloc.toStatementValue());
        compileStatementValue(state, left);
        compileStatementValue(state, right);
        return retloc.toStatementValue();
    }
    
    public static final StatementValue compileMul(CompilerState state, StatementTask op1, StatementTask op2, StoringLocation retloc)
            throws CompilerException
    {
        return compileMulDiv(state, op1, op2, retloc, true);
    }
    
    public static final StatementValue compileDiv(CompilerState state, StatementTask op1, StatementTask op2, StoringLocation retloc)
            throws CompilerException
    {
        return compileMulDiv(state, op1, op2, retloc, false);
    }
}
