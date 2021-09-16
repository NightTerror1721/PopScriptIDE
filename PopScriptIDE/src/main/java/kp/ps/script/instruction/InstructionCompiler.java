/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.instruction;

import java.util.List;
import kp.ps.script.compiler.CodeManager;
import kp.ps.script.compiler.CompilerException;
import kp.ps.script.compiler.CompilerState;

/**
 *
 * @author Marc
 */
public class InstructionCompiler
{
    private InstructionCompiler() {}
    
    public static void normalCompile(CompilerState state, CodeManager code, List<Instruction> instructions)
    {
        for(Instruction inst : instructions)
        {
            try
            {
                inst.normalCompile(state, code);
            }
            catch(CompilerException ex)
            {
                state.getErrors().addError(state.getCurrentSourceFile(), inst.getFirstLine(), inst.getFirstLine(), ex);
            }
        }
    }
    
    public static void constCompile(CompilerState state, List<Instruction> instructions)
    {
        for(Instruction inst : instructions)
        {
            try
            {
                inst.constCompile(state);
            }
            catch(CompilerException ex)
            {
                state.getErrors().addError(state.getCurrentSourceFile(), inst.getFirstLine(), inst.getFirstLine(), ex);
            }
        }
    }
    
    public static void staticCompile(
            CompilerState state,
            CodeManager initCode,
            CodeManager mainCode,
            List<Instruction> instructions)
    {
        for(Instruction inst : instructions)
        {
            try
            {
                inst.staticCompile(state, initCode, mainCode);
            }
            catch(CompilerException ex)
            {
                state.getErrors().addError(state.getCurrentSourceFile(), inst.getFirstLine(), inst.getFirstLine(), ex);
            }
        }
    }
}
