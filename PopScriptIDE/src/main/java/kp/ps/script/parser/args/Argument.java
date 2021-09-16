/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser.args;

import kp.ps.script.parser.ElementReference;
import kp.ps.script.parser.Identifier;
import kp.ps.script.parser.Statement;
import kp.ps.script.parser.Type;

/**
 *
 * @author Marc
 */
public abstract class Argument
{
    public boolean isCallArgument() { return false; }
    public boolean isDeclarationArgument() { return false; }
    
    public Statement getStatement() { throw new UnsupportedOperationException(); }
    
    public Type getDeclarationType() { throw new UnsupportedOperationException(); }
    public Identifier getDeclarationIdentifier() { throw new UnsupportedOperationException(); }
    public ElementReference getDeclarationDefaultValue() { throw new UnsupportedOperationException(); }
    
    public static final Argument call(Statement statement) { return new CallArgument(statement); }
    public static final Argument declaration(Type type, Identifier identifier, ElementReference defaultValue)
    {
        return new DeclarationArgument(type, identifier, defaultValue);
    }
    
    @Override
    public abstract String toString();
    
    @Override
    public abstract boolean equals(Object o);
    
    @Override
    public abstract int hashCode();
}
