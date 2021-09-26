/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.functions;

import java.util.Arrays;
import java.util.Objects;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.script.compiler.statement.utils.StatementTaskUtils;
import kp.ps.script.compiler.statement.utils.TemporaryVars;
import kp.ps.script.compiler.types.ParameterType;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.script.instruction.Instruction;
import kp.ps.script.instruction.InstructionCompiler;
import kp.ps.script.parser.ArgumentList;
import kp.ps.script.parser.CodeParser;
import kp.ps.script.parser.Command;
import kp.ps.script.parser.CommandId;
import kp.ps.script.parser.FragmentList;
import kp.ps.script.parser.Identifier;
import kp.ps.script.parser.Scope;
import kp.ps.script.parser.args.Argument;
import kp.ps.utils.CodeReader;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class Macro
{
    private final String name;
    private final String description;
    private final Parameter[] params;
    private final Instruction[] instructions;
    private final int firstLine;
    private final int lastLine;
    private final int numOfMandatoryArgs;
    private final boolean hasYield;
    
    private Macro(String name, String description, Parameter[] pars, Instruction[] instructions, int firstLine, int lastLine)
    {
        this.name = Objects.requireNonNull(name);
        this.description = prepareDescription(description);
        this.params = Objects.requireNonNull(pars);
        this.instructions = Objects.requireNonNull(instructions);
        this.firstLine = firstLine;
        this.lastLine = lastLine;
        
        int mandatory = 0;
        for(Parameter p : this.params)
        {
            if(p.hasDefaultValue())
                mandatory++;
            else if(mandatory > 0)
                throw new IllegalStateException();
        }
        this.numOfMandatoryArgs = mandatory;
        
        boolean yield = false;
        for(Instruction inst : this.instructions)
            if(inst.hasYieldInstruction())
                yield = true;
        this.hasYield = yield;
    }
    
    public final String getName() { return name; }
    public final String getDescription() { return description; }
    public final int getParameterCount() { return params.length; }
    public final Parameter getParameter(int index) { return params[index]; }
    
    public final Instruction[] getInstructions() { return Arrays.copyOf(instructions, instructions.length); }
    
    public final boolean hasYield() { return hasYield; }
    
    public final int getFirstLine() { return firstLine; }
    public final int getLastLine() { return lastLine; }
    
    public final MemoryAddress compile(CompilerState state, CodeManager code, StatementTask[] argTasks, MemoryAddress retloc) throws CompilerException
    {
        if(argTasks.length > params.length)
            throw new CompilerException("Expected only %s num of args. But found %s.", params.length, argTasks.length);
        
        if(argTasks.length < numOfMandatoryArgs)
            throw new CompilerException("Expected at least %s num of args. But found %s.", numOfMandatoryArgs, argTasks.length);
        
        CodeManager prevCode = new CodeManager();
        StatementValue[] args = new StatementValue[params.length];
        try(TemporaryVars temps = TemporaryVars.open(state, prevCode))
        {
            MemoryAddress yieldloc = retloc.isInvalid()
                    ? temps.push()
                    : retloc;
            state.pushMacroInvocation(this, yieldloc);
            
            for(int i = 0; i < params.length; ++i)
            {
                StatementValue valueArg = i >= argTasks.length
                        ? null
                        : temps.argCompileWithTemp(argTasks[i]);
                args[i] = params[i].checkOrGetDefault(valueArg);
            }
            
            code.insertCode(prevCode);
            
            state.pushLocalElements();
            for(int i = 0; i < args.length; ++i)
                state.getLocalElements().createArgument(params[i].getName(), args[i]);
            InstructionCompiler.normalCompile(state, code, Arrays.asList(instructions));
            state.popLocalElements();
            
            state.popMacroInvocation();
            
            if(!hasYield && retloc == yieldloc)
                StatementTaskUtils.assignation(yieldloc, StatementValue.of(Int32.ZERO)).normalCompile(state, code);
            
            return yieldloc;
        }
    }
    
    public static final Macro parse(CompilerState state, CodeReader reader, CodeParser parser, ErrorList errors) throws CompilerException
    {
        int first = reader.getCurrentLine();
        FragmentList frags = parser.parseUntilScopeAsList(reader, Command.fromId(CommandId.MACRO), errors);
        int last = reader.getCurrentLine();
        
        if(frags.size() != 3)
            throw new CompilerException("Malformed macro declaration. Expected macro <identifier>(<args>...).");
        
        if(!frags.get(0).isIdentifier())
            throw new CompilerException("Expected valid identifier in macro declaration. But found '%s'.", frags.get(0));
        
        if(!frags.get(1).isArgumentList())
            throw new CompilerException("Expected valid parameters list in macro declaration. But found '%s'.", frags.get(1));
        
        if(!frags.get(2).isScope())
            throw new CompilerException("Expected valid scope at end of macro declaration. But found '%s'.", frags.get(2));
        
        Identifier ident = frags.get(0);
        ArgumentList args = frags.get(1);
        Scope scope = frags.get(2);
        
        if(!args.isDeclarationArguments())
            throw new CompilerException("Expected valid declaration parameters list in macro declaration. But found '%s'.", args);
        
        Parameter[] pars = new Parameter[args.size()];
        for(int i = 0; i < pars.length; ++i)
        {
            Argument arg = args.getArgument(i);
            if(!arg.isDeclarationArgument())
                throw new IllegalStateException();
            
            ParameterType type = arg.getDeclarationType().getParameterType();
            String name = arg.getDeclarationIdentifier().toString();
            StatementValue defaultValue = arg.getDeclarationDefaultValue() == null
                    ? null
                    : StatementValue.decode(state, arg.getDeclarationDefaultValue());
            
            if(defaultValue != null && !type.isCompatible(defaultValue.getCompleteType()))
                throw new CompilerException("Cannot assign '%s' to '%s' in defaut value declaration '%s'.",
                        defaultValue.getCompleteType(), type, arg);
            
            pars[i] = createParameter(type, name, defaultValue);
        }
        
        return new Macro(ident.toString(), parser.getStoredComment(), pars, scope.getInstructions(), first, last);
    }
    
    private static Parameter createParameter(ParameterType type, String name, StatementValue defaultValue) throws CompilerException
    {
        if(type.getType() == TypeId.INT)
        {
            if(type.hasModifier())
            {
                switch(type.getModifier())
                {
                    case VAR:
                        if(defaultValue == null)
                            return Parameter.variable(name);
                        throw new CompilerException("var int type cannot has default value.");

                    case CONST:
                        if(defaultValue == null)
                            return Parameter.constant(name);
                        return Parameter.constant(name, defaultValue.getConstantValue());

                    case INTERNAL:
                        if(defaultValue == null)
                            return Parameter.internal(name);
                        return Parameter.internal(name, defaultValue.getInternal());
                }
            }
            else
            {
                if(defaultValue == null)
                    return Parameter.integer(name);
                return Parameter.integer(name, defaultValue.getConstantValue());
            }
        }
        else
        {
            if(defaultValue == null)
                return Parameter.typed(name, type.getType());
            return Parameter.typed(name, type.getType(), defaultValue.getTypedValue());
        }
        
        throw new IllegalStateException();
    }
    
    private static String prepareDescription(String description)
    {
        if(description == null)
            return "";
        
        description = description.trim();
        char[] array = description.toCharArray();
        int front = 0, back = array.length - 1;
        
        for(; front <= back; ++front)
            if(array[front] != '\n' && array[front] != '\r')
                break;
        
        for(; front <= back; --back)
            if(array[back] != '\n' && array[back] != '\r')
                break;
        
        if(front > back)
            return "";
        return new String(array, front, (back - front) + 1);
    }
}
