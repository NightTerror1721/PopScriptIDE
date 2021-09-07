/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.functions.actions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;
import kp.ps.script.compiler.functions.Parameter;
import kp.ps.script.compiler.statement.MemoryAddress;
import kp.ps.script.compiler.statement.StatementTask;
import kp.ps.script.compiler.statement.StatementValue;
import kp.ps.script.compiler.statement.utils.TemporaryVars;
import kp.ps.utils.Pair;
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public class InnerFunction
{
    private final ScriptToken action;
    private final List<Parameter> parameters;
    private final Map<String, Parameter> mappedParameters;
    private final int numOfMandatoryArgs;
    private final boolean hasReturn;
    
    InnerFunction(ScriptToken action, boolean hasReturn, Parameter... parameters)
    {
        if(!action.isCommand())
            throw new IllegalStateException();
        
        this.action = action;
        this.parameters = parameters == null || parameters.length == 0
                ? new LinkedList<>()
                : Stream.of(parameters)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        this.mappedParameters = this.parameters.stream()
                .collect(Collectors.toMap(Parameter::getName, Utils::self));
        
        int mandatory = 0;
        for(Parameter p : this.parameters)
        {
            if(p.hasDefaultValue())
                mandatory++;
            else if(mandatory > 0)
                throw new IllegalStateException();
        }
        this.numOfMandatoryArgs = mandatory;
        
        this.hasReturn = hasReturn;
    }
    
    public final String getName() { return action.getLangName(); }
    
    public final ScriptToken getAction() { return action; }
    
    public final int getParametersCount() { return parameters.size(); }
    public final Parameter getParameter(int index) { return parameters.get(index); }
    public final Parameter getParameterByName(String name)
    {
        return mappedParameters.getOrDefault(name, null);
    }
    
    public final void checkParameterTypes(List<StatementValue> args) throws CompilerException
    {
        if(args.size() > parameters.size())
            throw new CompilerException("Expected only %s num of args. But found %s.", parameters.size(), args.size());
        
        if(args.size() < numOfMandatoryArgs)
            throw new CompilerException("Expected at least %s num of args. But found %s.", numOfMandatoryArgs, args.size());
        
        for(Pair<StatementValue, Parameter> pair : Utils.iterableOf(args, parameters))
            pair.right.check(pair.left);
    }
    
    public final MemoryAddress compile(CompilerState state, CodeManager code, StatementTask[] argTaks, MemoryAddress retloc) throws CompilerException
    {
        if(argTaks.length > parameters.size())
            throw new CompilerException("Expected only %s num of args. But found %s.", parameters.size(), argTaks.length);
        
        if(argTaks.length < numOfMandatoryArgs)
            throw new CompilerException("Expected at least %s num of args. But found %s.", numOfMandatoryArgs, argTaks.length);
        
        CodeManager tempCode = new CodeManager();
        try(TemporaryVars temps = TemporaryVars.open(state, tempCode))
        {
            int argId = 0;
            for(Parameter par : parameters)
            {
                StatementValue valueArg = argId >= argTaks.length
                        ? null
                        : temps.argCompileWithTemp(argTaks[argId++]);
                par.compile(state, code, valueArg);
            }
            
            if(hasReturn)
            {
                if(retloc.isInvalid())
                {
                    temps.push().compileWrite(state, tempCode);
                    temps.pop();
                }
                else
                {
                    retloc.compileWrite(state, code);
                }
            }
            else if(!retloc.isInvalid())
                throw new CompilerException("Cannot assign returned data from 'void' return type functions.");
            
            code.insertTokenCode(ScriptToken.DO);
            code.insertTokenCode(action);
            code.insertCode(tempCode);
        }
        
        return retloc;
    }
    
    @Override
    public final String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(hasReturn ? "int " : "void ").append(getName()).append('(');
        sb.append(parameters.stream().map(Parameter::toString).collect(Collectors.joining(", ")));
        sb.append(");");
        
        return sb.toString();
    }
}
