/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import kp.ps.script.compiler.CompilerState;

/**
 *
 * @author mpasc
 */
public abstract class Instruction
{
    public abstract void compile(CompilerState state);
}
