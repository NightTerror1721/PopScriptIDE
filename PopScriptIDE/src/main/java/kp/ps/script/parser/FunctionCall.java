/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

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
    
}
