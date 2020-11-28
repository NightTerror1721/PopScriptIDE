/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser.args;

import java.util.Objects;
import kp.ps.script.parser.Statement;

/**
 *
 * @author Marc
 */
final class CallArgument extends Argument
{
    private final Statement statement;
    
    CallArgument(Statement statement) { this.statement = Objects.requireNonNull(statement); }
    
    @Override
    public final boolean isCallArgument() { return true; }
    
    @Override
    public final Statement getStatement() { return statement; }
    
    @Override
    public final String toString() { return statement.toString(); }
    
    @Override
    public final boolean equals(Object o)
    {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(o instanceof CallArgument)
            return statement.equals(((CallArgument) o).statement);
        return false;
    }

    @Override
    public final int hashCode()
    {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.statement);
        return hash;
    }
}
