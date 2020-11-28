/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Marc
 */
public class FunctionCall extends Statement
{
    private final Identifier identifier;
    private final Statement[] args;
    
    public FunctionCall(Fragment identifier, Fragment args) throws SyntaxException
    {
        if(!identifier.isIdentifier())
            throw new SyntaxException("Can only call functions stored in valid identifiers.");
        if(!args.isArgumentList() || !((ArgumentList) args).isCallArguments())
            throw new SyntaxException("Expected valid arguments list after function call operator.");
        
        this.identifier = (Identifier) identifier;
        this.args = ((ArgumentList) args).getArguments().stream()
                .map(a -> a.getStatement())
                .toArray(Statement[]::new);
    }
    
    public final Identifier getIdentifier() { return identifier; }
    
    public final int getArgumentCount() { return args.length; }
    public final Statement getArgument(int index) { return args[index]; }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.FUNCTION_CALL; }

    @Override
    public final String toString()
    {
        return identifier + Stream.of(args)
                .map(Statement::toString)
                .collect(Collectors.joining(", ", "(", ")"));
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof FunctionCall)
        {
            FunctionCall fc = (FunctionCall) o;
            return identifier.equals(fc.identifier) &&
                    Arrays.equals(args, fc.args);
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.identifier);
        hash = 29 * hash + Arrays.deepHashCode(this.args);
        return hash;
    }
}
