/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.decompiler;

import java.io.OutputStream;
import kp.ps.script.Script;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.functions.InnerFunction;
import kp.ps.script.compiler.functions.InnerFunctionPool;
import kp.ps.script.compiler.functions.Parameter;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.parser.CommandId;
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
final class ScriptDecompilerImpl
{
    private ScriptDecompilerImpl() {}
    
    private static final int IDENTATION_SIZE = 4;
    
    public static final void decompile(Script script, OutputStream output)
    {
        try(DecompilerState state = new DecompilerState(script, output))
        {
            try
            {
                state.checkNextCommand(ScriptToken.BEGIN);
                state.append(CommandId.MAIN.getCommandName())
                        .append(' ')
                        .append('{')
                        .println();
                state.printVariables(IDENTATION_SIZE);
                state.println();
                decompileScope(state, 0);
                state.checkNextCommand(ScriptToken.SCRIPT_END);
            }
            catch(DecompilerException ex)
            {
                ex.printError(state, 0);
            }
        }
    }
    
    private static void decompileScope(DecompilerState state, int identation) throws DecompilerException
    {
        String sident = Utils.stringDup(' ', identation);
        int newIdentation = identation + IDENTATION_SIZE;
        String snewident = Utils.stringDup(' ', newIdentation);
        
        main_loop:
        for(;;)
        {
            if(!state.hasMoreCodes())
                throw new DecompilerException("Unexpected end of file.");
            try
            {
                ScriptToken command = state.nextCommand();
                switch(command)
                {
                    case IF:
                        decompileIf(state, newIdentation);
                        break;
                    
                    case DO:
                        decompileDo(state, newIdentation);
                        break;
                        
                    case SET:
                    case INCREMENT:
                    case DECREMENT:
                        decompileSetIncDec(state, snewident, command);
                        break;
                        
                    case MULTIPLY:
                    case DIVIDE:
                        decompileMulDiv(state, snewident, command);
                        break;
                        
                    case EVERY:
                        decompileEvery(state, newIdentation);
                        break;
                        
                    case END:
                        break main_loop;
                        
                    default:
                        throw new DecompilerException("Unexpected token '%h'. Expected valid command token.", command.getCode().toInt());
                }
            }
            catch(DecompilerException ex)
            {
                ex.printError(state, newIdentation);
            }
        }
        
        state.append(sident)
                    .append('}')
                    .println();
    }
    
    private static void decompileSetIncDec(DecompilerState state, String sident, ScriptToken command) throws DecompilerException
    {
        Field left = state.nextField();
        Field right = state.nextField();
        String operator;
        
        switch(command)
        {
            case SET: operator = " = "; break;
            case INCREMENT: operator = " += "; break;
            case DECREMENT: operator = " -= "; break;
            default:
                throw new IllegalStateException();
        }
        
        state.append(sident)
                .append(left.toString())
                .append(operator)
                .append(right.toString())
                .append(';')
                .println();
    }
    
    private static void decompileMulDiv(DecompilerState state, String sident, ScriptToken command) throws DecompilerException
    {
        Field alloc = state.nextField();
        Field left = state.nextField();
        Field right = state.nextField();
        String operator;
        
        switch(command)
        {
            case MULTIPLY: operator = " * "; break;
            case DIVIDE: operator = " / "; break;
            default:
                throw new IllegalStateException();
        }
        
        state.append(sident)
                .append(alloc.toString())
                .append(" = ")
                .append(left.toString())
                .append(operator)
                .append(right.toString())
                .append(';')
                .println();
    }
    
    private static void decompileEvery(DecompilerState state, int identation) throws DecompilerException
    {
        Field mode = state.nextField();
        if(!mode.isConstant())
            throw new DecompilerException("Unexpected non constant field in 'every' first parameter.");
        
        int modeValue = mode.getConstantValue() + 1;
        switch(modeValue)
        {
            case 2:
            case 4:
            case 8:
            case 16:
            case 32:
            case 64:
            case 128:
            case 256:
            case 512:
            case 1024:
            case 2048:
            case 4096:
            case 8192:
                break;
                
            default:
                throw new DecompilerException("Unexpected constant value in 'every' first parameter. Possible values are "
                        + "[2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192]. But found '%s'.", modeValue);
        }
        
        Field delay = state.nextEverySecondParam();
        
        String sident = Utils.stringDup(' ', identation);
        if(delay != null)
        {
            if(!delay.isConstant())
                throw new DecompilerException("Unexpected non constant field in 'every' second parameter.");

            int delayValue = delay.getConstantValue() + 1;
            if(delayValue < 1 || delayValue >= modeValue)
                throw new DecompilerException("Unexpected constant value in 'every' first parameter. "
                        + "Excepted value of range [%s, %s] but found %s.",
                        1, modeValue - 1, delayValue);
            
            state.checkNextCommand(ScriptToken.BEGIN);
            
            state.append(sident)
                    .append(CommandId.EVERY.getCommandName())
                    .append('(')
                    .append(Integer.toString(modeValue))
                    .append(", ")
                    .append(Integer.toString(delayValue))
                    .append(") {")
                    .println();
        }
        else
        {
            state.append(sident)
                    .append(CommandId.EVERY.getCommandName())
                    .append('(')
                    .append(Integer.toString(modeValue))
                    .append(") {")
                    .println();
        }
        
        decompileScope(state, identation);
    }
    
    private static void decompileDo(DecompilerState state, int identation) throws DecompilerException
    {
        ScriptToken action = state.nextAction().getToken();
        if(action == ScriptToken.ATTACK)
            System.out.println();
        if(!InnerFunctionPool.exists(action))
            throw new DecompilerException("Illegal action token '%s'.", action.getLangName());
        
        StringBuilder sb = new StringBuilder();
        
        InnerFunction function = InnerFunctionPool.get(action);
        sb.append(function.getName()).append('(');
        
        int len = function.getParametersCount();
        for(int i = 0; i < len; ++i)
        {
            if(i > 0)
                sb.append(", ");
            sb.append(decompileDoParameter(state, function.getParameter(i)));
        }
        
        sb.append(");");
        
        if(function.hasReturn())
        {
            Field field = state.nextField();
            if(!field.isUser())
                throw new DecompilerException("Expected valid variable field in function return point. But found '%s'.", field);
            
            state.append(Utils.stringDup(' ', identation))
                    .append(field.toString())
                    .append(" = ")
                    .append(sb)
                    .println();
        }
        else state.append(Utils.stringDup(' ', identation))
                .append(sb.toString())
                .println();
    }
    
    private static String decompileDoParameter(DecompilerState state, Parameter par) throws DecompilerException
    {
        if(par.getType().getType() == TypeId.INT)
        {
            Field field = state.nextField();
            if(par.getType().hasModifier())
            {
                switch(par.getType().getModifier())
                {
                    case VAR:
                        if(!field.isUser())
                            throw new DecompilerException("Expected valid variable field. But found '%s'.", field.toString());
                        break;
                        
                    case CONST:
                        if(!field.isConstant())
                            throw new DecompilerException("Expected valid constant field. But found '%s'.", field.toString());
                        break;
                        
                    case INTERNAL:
                        if(!field.isInternal())
                            throw new DecompilerException("Expected valid internal field. But found '%s'.", field.toString());
                        break;
                }
            }
            return field.toString();
        }
        
        return state.nextTypedValue(par.getType().getType()).getName();
    }
    
    private static void decompileIf(DecompilerState state, int identation) throws DecompilerException
    {
        String comd = decompileCondition(state);
        
        state.checkNextCommand(ScriptToken.BEGIN);
        
        state.append(Utils.stringDup(' ', identation))
                .append(CommandId.IF.getCommandName())
                .append('(')
                .append(comd)
                .append(") {")
                .println();
        decompileScope(state, identation);
        
        if(state.nextCommand(ScriptToken.ELSE, ScriptToken.ENDIF) == ScriptToken.ELSE)
        {
            state.checkNextCommand(ScriptToken.BEGIN);
            state.append(Utils.stringDup(' ', identation))
                    .append(CommandId.ELSE.getCommandName())
                    .append(" {")
                    .println();
            decompileScope(state, identation);
            state.checkNextCommand(ScriptToken.ENDIF);
        }
    }
    
    private static final ScriptToken[] COND_OPS = {
        ScriptToken.AND, ScriptToken.OR,
        ScriptToken.EQUAL_TO, ScriptToken.NOT_EQUAL_TO,
        ScriptToken.GREATER_THAN, ScriptToken.GREATER_THAN_EQUAL_TO,
        ScriptToken.LESS_THAN, ScriptToken.LESS_THAN_EQUAL_TO
    };
    private static String decompileCondition(DecompilerState state) throws DecompilerException
    {
        ScriptToken op = state.nextCommand(COND_OPS);
        if(op == ScriptToken.AND || op == ScriptToken.OR)
            return decompileAndOrCondition(state, op);
        return decompileNormalCondition(state, op);
    }
    
    private static String decompileNormalCondition(DecompilerState state, ScriptToken command) throws DecompilerException
    {
        Field left = state.nextField();
        Field right = state.nextField();
        
        switch(command)
        {
            case EQUAL_TO: return left + " == " + right;
            case NOT_EQUAL_TO: return left + " != " + right;
            case GREATER_THAN: return left + " > " + right;
            case GREATER_THAN_EQUAL_TO: return left + " >= " + right;
            case LESS_THAN: return left + " < " + right;
            case LESS_THAN_EQUAL_TO: return left + " <= " + right;
        }
        
        throw new IllegalStateException();
    }
    
    private static String decompileAndOrCondition(DecompilerState state, ScriptToken command) throws DecompilerException
    {
        String left = decompileCondition(state);
        String right = decompileCondition(state);
        
        switch(command)
        {
            case AND: return '(' + left + " && " + right + ')';
            case OR: return '(' + left + " || " + right + ')';
        }
        
        throw new IllegalStateException();
    }
}
