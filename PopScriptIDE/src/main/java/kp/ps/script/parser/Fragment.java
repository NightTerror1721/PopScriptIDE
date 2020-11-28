/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.parser;

/**
 *
 * @author Marc
 */
public abstract class Fragment
{
    public abstract FragmentType getFragmentType();
    
    public abstract boolean isStatement();
    
    @Override
    public abstract String toString();
    
    
    public final boolean isIdentifier() { return getFragmentType() == FragmentType.IDENTIFIER; }
    public final boolean isCommand() { return getFragmentType() == FragmentType.COMMAND; }
    public final boolean isType() { return getFragmentType() == FragmentType.TYPE; }
    public final boolean isLiteral() { return getFragmentType() == FragmentType.LITERAL; }
    public final boolean isOperator() { return getFragmentType() == FragmentType.OPERATOR; }
    public final boolean isOperation() { return getFragmentType() == FragmentType.OPERATION; }
    public final boolean isAssignmentOperation() { return getFragmentType() == FragmentType.ASSIGNMENT; }
    public final boolean isFunctionCallOperation() { return getFragmentType() == FragmentType.FUNCTION_CALL; }
    public final boolean isNamespaceResolverOperation() { return getFragmentType() == FragmentType.NAMESPACE_RESOLVER; }
    public final boolean isArgumentList() { return getFragmentType() == FragmentType.ARGUMENT_LIST; }
    public final boolean isSeparator() { return getFragmentType() == FragmentType.SEPARATOR; }
    public final boolean isScope() { return getFragmentType() == FragmentType.SCOPE; }
}
