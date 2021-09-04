/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.statement;

import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.statement.utils.StatementTaskUtils;
import kp.ps.script.parser.Operation;
import kp.ps.script.parser.Operator;
import kp.ps.script.parser.Statement;

/**
 *
 * @author Marc
 */
public final class StatementCompiler
{
    private StatementCompiler() {}
    
    public static final StatementValue resolveIdentifier(CompilerState state, Statement identifier) throws CompilerException
    {
        if(!identifier.isIdentifier())
            throw new CompilerException("Expected valid identifier, but found '" + identifier + "'.");
        
        return StatementValue.decode(state, (Statement) identifier);
    }
    
    public static final StatementTask.ConditionalState compileIfCommand(CompilerState state, CodeManager code, StatementTask condition) throws CompilerException
    {
        CodeManager prev = new CodeManager();
        CodeManager cond = new CodeManager();
        
        StatementTask.ConditionalState result = condition.conditionalCompile(state, prev, cond);
        if(result != StatementTask.ConditionalState.UNKNOWN)
            return result;
        
        code.insertCode(prev);
        code.insertTokenCode(ScriptToken.IF);
        code.insertCode(cond);
        
        return result;
    }
    
    public static final StatementTask toTask(CompilerState state, Statement statement) throws CompilerException
    {
        switch(statement.getFragmentType())
        {
            case IDENTIFIER:
            case LITERAL:
            case NAMESPACE_RESOLVER:
                return StatementValue.decode(state, statement);
                
            case OPERATION: {
                Operation operation = (Operation) statement;
                if(operation.isUnary())
                    return toTaskUnaryOperator(state, operation);
                else if(operation.isBinary())
                    return toTaskBinaryOperator(state, operation);
                else if(operation.isTernary())
                {
                    return StatementTaskUtils.elvis(
                            toTask(state, operation.getTernaryCondition()),
                            toTask(state, operation.getTernaryTrueAction()),
                            toTask(state, operation.getTernaryFalseAction())
                    );
                }
            } break;
        }
        
        throw new CompilerException("Unexpected token '%s'", statement);
    }
    
    private static StatementTask toTaskUnaryOperator(CompilerState state, Operation operation) throws CompilerException
    {
        Operator op = operation.getOperator();
        StatementTask operand = toTask(state, operation.getUnaryOperand());
        
        switch(op.getOperatorId())
        {
            case SUFFIX_INCREASE:
                return StatementTaskUtils.inc(operand, false);
                
            case SUFFIX_DECREASE:
                return StatementTaskUtils.dec(operand, false);
                
            case PREFIX_INCREASE:
                return StatementTaskUtils.inc(operand, true);
                
            case PREFIX_DECREASE:
                return StatementTaskUtils.dec(operand, true);
                
            case LOGICAL_NOT:
                return StatementTaskUtils.not(operand);
                
            case UNARY_MINUS:
                return StatementTaskUtils.negative(operand);
        }
        
        throw new IllegalStateException();
    }
    
    private static StatementTask toTaskBinaryOperator(CompilerState state, Operation operation) throws CompilerException
    {
        Operator op = operation.getOperator();
        StatementTask left = toTask(state, operation.getBinaryLeftOperand());
        StatementTask right = toTask(state, operation.getBinaryRightOperand());
        
        switch(op.getOperatorId())
        {
            case MULTIPPLY:
                return StatementTaskUtils.mul(left, right);
                
            case DIVIDE:
                return StatementTaskUtils.div(left, right);
                
            case ADD:
                return StatementTaskUtils.sum(left, right);
                
            case SUBTRACT:
                return StatementTaskUtils.sub(left, right);
                
            case GREATER:
                return StatementTaskUtils.greater(left, right);
                
            case LESS:
                return StatementTaskUtils.less(left, right);
                
            case GREATER_EQUALS:
                return StatementTaskUtils.greaterOrEquals(left, right);
                
            case LESS_EQUALS:
                return StatementTaskUtils.lessOrEquals(left, right);
                
            case EQUALS:
                return StatementTaskUtils.equals(left, right);
                
            case DIFFERENT:
                return StatementTaskUtils.notEquals(left, right);
                
            case AND:
                return StatementTaskUtils.and(left, right);
                
            case OR:
                return StatementTaskUtils.or(left, right);
                
            case ASSIGNATION:
                return StatementTaskUtils.assignation(left, right);
                
            case ASSIGNATION_ADD:
                return StatementTaskUtils.assignationSum(left, right);
                
            case ASSIGNATION_SUBTRACT:
                return StatementTaskUtils.assignationSub(left, right);
                
            case ASSIGNATION_MULTIPLY:
                return StatementTaskUtils.assignationMul(left, right);
                
            case ASSIGNATION_DIVIDE:
                return StatementTaskUtils.assignationDiv(left, right);
        }
        
        throw new IllegalStateException();
    }
}
