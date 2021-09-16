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
import kp.ps.script.compiler.LocalElementsScope.Element;
import kp.ps.script.compiler.functions.InnerFunction;
import kp.ps.script.compiler.functions.InnerFunctionPool;
import kp.ps.script.compiler.functions.Macro;
import kp.ps.script.compiler.statement.StatementTask.ConditionalState;
import kp.ps.script.compiler.statement.utils.StatementTaskUtils;
import kp.ps.script.compiler.statement.utils.TemporaryVars;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.instruction.InstructionCompiler;
import kp.ps.script.namespace.Namespace;
import kp.ps.script.namespace.NamespaceField;
import kp.ps.script.parser.ElementReference;
import kp.ps.script.parser.Fragment;
import kp.ps.script.parser.FunctionCall;
import kp.ps.script.parser.NamespaceResolver;
import kp.ps.script.parser.Operation;
import kp.ps.script.parser.Operator;
import kp.ps.script.parser.Scope;
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
        ConditionalState result;
        try(TemporaryVars temps = TemporaryVars.open(state, prev))
        {
            result = condition.conditionalCompile(state, prev, cond, temps);
        }
        if(result != ConditionalState.UNKNOWN)
            return result;
        
        code.insertCode(prev);
        code.insertTokenCode(ScriptToken.IF);
        code.insertCode(cond);
        
        return result;
    }
    
    public static final void compileScope(CompilerState state, CodeManager code, Fragment statement) throws CompilerException
    {
        compileScope(state, code, statement, false);
    }
    
    public static final void compileScope(CompilerState state, CodeManager code, Fragment statement, boolean fakeScope) throws CompilerException
    {
        if(statement.isScope())
        {
            Scope scope = (Scope) statement;
            state.pushLocalElements();
            if(!fakeScope)
                code.insertTokenCode(ScriptToken.BEGIN);
            InstructionCompiler.normalCompile(state, code, scope.getInstructionsAsList());
            if(!fakeScope)
                code.insertTokenCode(ScriptToken.END);
            state.popLocalElements();
        }
        else if(statement.isStatement())
        {
            StatementTask task = toTask(state, ((Statement) statement));
            state.pushLocalElements();
            if(!fakeScope)
                code.insertTokenCode(ScriptToken.BEGIN);
            task.normalCompile(state, code);
            if(!fakeScope)
                code.insertTokenCode(ScriptToken.END);
            state.popLocalElements();
        }
        else throw new CompilerException("Expected valid scope, but found '%s'.", statement);
    }
    
    public static final void compileConstScope(CompilerState state, Fragment statement) throws CompilerException
    {
        if(statement.isScope())
        {
            Scope scope = (Scope) statement;
            state.pushLocalElements();
            InstructionCompiler.constCompile(state, scope.getInstructionsAsList());
            state.popLocalElements();
        }
        else if(statement.isStatement())
        {
            StatementTask task = toTask(state, ((Statement) statement));
            state.pushLocalElements();
            task.constCompile();
            state.popLocalElements();
        }
        else throw new CompilerException("Expected valid scope, but found '%s'.", statement);
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
            
            case FUNCTION_CALL:
                return toTaskCall(state, (FunctionCall) statement);
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
    
    private static StatementTask toTaskCall(CompilerState state, FunctionCall call) throws CompilerException
    {
        ElementReference ref = call.getIdentifier();
        if(ref.isIdentifier())
        {
            String identifier = ref.toString();
            if(state.getNamespace().existsMacro(identifier))
                return toTaskCallAction(state, call, state.getNamespace().getMacro(identifier));
            
            if(state.getLocalElements().exists(identifier))
            {
                Element element = state.getLocalElements().get(identifier);
                if(element.getType() == TypeId.ACTION)
                    return toTaskCallAction(state, call, element.getTypedValue().getToken());
            }
            
            if(state.getNamespace().existsField(identifier))
            {
                NamespaceField field = state.getNamespace().getField(identifier);
                if(field.getType() == TypeId.ACTION)
                    return toTaskCallAction(state, call, field.getTypedValue().getToken());
            }
            
            throw new CompilerException("%s is not a valid callable element (can call only actions and macros).", ref);
        }
        else
        {
            NamespaceResolver resolver = ref.getNamespaceResolver();
            Namespace namespace = resolver.findNamespace(state.getNamespace());
            String identifier = resolver.getLastIdentifier().toString();
            
            if(namespace.existsMacro(identifier))
                return toTaskCallAction(state, call, namespace.getMacro(identifier));
            
            if(namespace.existsField(identifier))
            {
                NamespaceField field = namespace.getField(identifier);
                if(field.getType() == TypeId.ACTION)
                    return toTaskCallAction(state, call, field.getTypedValue().getToken());
            }
            
            throw new CompilerException("%s is not a valid callable element (can call only actions and macros).", ref);
        }
    }
    
    private static StatementTask toTaskCallAction(CompilerState state, FunctionCall call, ScriptToken action) throws CompilerException
    {
        if(!InnerFunctionPool.exists(action))
            throw new CompilerException("%s is not a valid callable element (can call only actions and macros).", call.getIdentifier());
        
        InnerFunction function = InnerFunctionPool.get(action);
        Statement[] sargs = call.getAllArguments();
        StatementTask[] args = new StatementTask[sargs.length];
        
        for(int i = 0; i < args.length; ++i)
            args[i] = toTask(state, sargs[i]);
        
        return StatementTaskUtils.actionCall(function, args);
    }
    
    private static StatementTask toTaskCallAction(CompilerState state, FunctionCall call, Macro macro) throws CompilerException
    {
        Statement[] sargs = call.getAllArguments();
        StatementTask[] args = new StatementTask[sargs.length];
        
        for(int i = 0; i < args.length; ++i)
            args[i] = toTask(state, sargs[i]);
        
        return StatementTaskUtils.macroCall(macro, args);
    }
}
