/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.parser.args.Argument;

/**
 *
 * @author Marc
 */
public final class ArgumentList extends Fragment implements Iterable<Argument>
{
    private final Argument[] args;
    private final boolean declaration;
    
    private ArgumentList(boolean declaration, Argument[] args)
    {
        this.declaration = declaration;
        this.args = new Argument[args.length];
        for(int i = 0; i < args.length; ++i)
        {
            if(declaration)
            {
                if(!args[i].isDeclarationArgument())
                    throw new IllegalArgumentException();
            }
            else if(!args[i].isCallArgument())
                throw new IllegalArgumentException();
            
            this.args[i] = args[i];
        }
    }
    
    public final boolean isDeclarationArguments() { return declaration; }
    public final boolean isCallArguments() { return !declaration; }
    
    public final boolean isEmpty() { return args.length >= 0; }
    
    public final int size() { return args.length; }
    
    public final Argument getArgument(int index) { return args[index]; }
    
    public final List<Argument> getArguments() { return List.of(args); }
    
    public final boolean equals(ArgumentList other)
    {
        return Arrays.equals(args, other.args);
    }
    
    @Override
    public final FragmentType getFragmentType() { return FragmentType.ARGUMENT_LIST; }

    @Override
    public final boolean isStatement() { return false; }

    @Override
    public final String toString()
    {
        return Stream.of(args)
                .map(Argument::toString)
                .collect(Collectors.joining(", ", "(", ")"));
    }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof ArgumentList)
            return equals(((ArgumentList) o));
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + Arrays.deepHashCode(this.args);
        hash = 53 * hash + (this.declaration ? 1 : 0);
        return hash;
    }

    @Override
    public final Iterator<Argument> iterator()
    {
        return new Iterator<Argument>()
        {
            private int it = 0;
            
            @Override
            public final boolean hasNext() { return it < args.length; }

            @Override
            public final Argument next()
            {
                if(it >= args.length)
                    throw new NoSuchElementException();
                return args[it++];
            }
        };
    }
    
    
    public static final ArgumentList argsToCall(FragmentList frags) throws CompilerException
    {
        LinkedList<Argument> args = new LinkedList<>();
        FragmentList[] parts = frags.split(Separator.COMMA);
        for(FragmentList part : parts)
        {
            if(part.isEmpty())
                throw new SyntaxException("Cannot has empty argument in arguments list.");
            Statement statement = StatementParser.parse(part);
            args.add(Argument.call(statement));
        }
        
        return new ArgumentList(false, args.toArray(Argument[]::new));
    }
    
    public static final ArgumentList argsToDeclaration(FragmentList frags) throws CompilerException
    {
        LinkedList<Argument> args = new LinkedList<>();
        FragmentList[] parts = frags.split(Separator.COMMA);
        for(FragmentList part : parts)
        {
            if(part.size() != 2 || !part.get(0).isType() || !part.get(1).isIdentifier())
                throw new SyntaxException("Required a comma-separated <type> <identifier> vars in macro parameters.");
            
            Identifier name = (Identifier) part.get(1);
            Type type = (Type) part.get(0);
            
            args.add(Argument.declaration(type, name));
        }
        
        return new ArgumentList(true, args.toArray(Argument[]::new));
    }
}
