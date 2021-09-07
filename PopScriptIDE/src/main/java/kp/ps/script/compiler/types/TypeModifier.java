/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler.types;

/**
 *
 * @author Marc
 */
public enum TypeModifier
{
    VAR("var"),
    CONST("const"),
    INTERNAL("internal");
    
    private final String name;
    
    private TypeModifier(String name)
    {
        this.name = name;
    }
    
    public final String getModifierName() { return name; }
}
