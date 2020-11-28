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
public enum FragmentType
{
    IDENTIFIER,
    COMMAND,
    TYPE,
    LITERAL,
    OPERATOR,
    OPERATION,
    ASSIGNMENT,
    FUNCTION_CALL,
    NAMESPACE_RESOLVER,
    ARGUMENT_LIST,
    SEPARATOR,
    SCOPE
}
