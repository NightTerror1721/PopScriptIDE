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
public enum CommandId
{
    IF("if"),
    ELSE("else"),
    EVERY("every"),
    MACRO("macro"),
    MAIN("main"),
    INIT("init"),
    IMPORT("import"),
    NAMESPACE("namespace"),
    YIELD("yield");
    
    private final String name;
    
    private CommandId(String name)
    {
        this.name = name;
    }
    
    public final String getCommandName() { return name; }
    
    @Override
    public final String toString() { return name; }
}
