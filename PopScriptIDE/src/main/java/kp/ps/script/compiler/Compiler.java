/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import kp.ps.script.Script;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.FieldsManager.FieldLocation;
import kp.ps.script.instruction.Instruction;
import kp.ps.script.instruction.InstructionCompiler;
import kp.ps.script.instruction.InstructionParser;
import kp.ps.utils.CodeReader;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class Compiler
{
    private Compiler() {}
    
    public static final Script compile(Path path, ErrorList errors) throws IOException
    {
        try(InputStream input = Files.newInputStream(path))
        {
            return compile(input, errors);
        }
    }
    
    public static final Script compile(String code, ErrorList errors)
    {
        return compile(new CodeReader(code), errors);
    }
    
    public static final Script compile(InputStream input, ErrorList errors)
    {
        return compile(new CodeReader(input), errors);
    }
    
    private static Script compile(CodeReader source, ErrorList errors)
    {
        if(errors == null)
            errors = new ErrorList();
        
        CompilerState state = new CompilerState(errors);
        CodeManager initCode = new CodeManager();
        CodeManager mainCode = new CodeManager();
        
        List<Instruction> insts = InstructionParser.parse(state, source, false, errors);
        InstructionCompiler.staticCompile(state, initCode, mainCode, insts);
        
        CodeManager code = new CodeManager();
        try
        {
            if(initCode.isEmpty())
            {
                code.insertTokenCode(ScriptToken.BEGIN);
                code.insertCode(mainCode);
                code.insertTokenCode(ScriptToken.END);
                code.insertTokenCode(ScriptToken.SCRIPT_END);
            }
            else
            {
                code.insertTokenCode(ScriptToken.BEGIN);
                insertSplittedParts(state, code, initCode, mainCode);
                code.insertTokenCode(ScriptToken.END);
                code.insertTokenCode(ScriptToken.SCRIPT_END);
            }
        }
        catch(CompilerException ex)
        {
            errors.addError(0, source.getCurrentLine(), ex);
        }
        
        Script script = new Script();
        state.getFields().insertToScript(script);
        code.insertToScript(script);
        
        state.clear();
        initCode.clear();
        mainCode.clear();
        code.clear();
        
        return script;
    }
    
    private static void insertSplittedParts(
            CompilerState state,
            CodeManager code,
            CodeManager initCode,
            CodeManager mainCode) throws CompilerException
    {
        FieldLocation locLeft = state.getFields().registerInternal(ScriptInternal.GAME_TURN);
        FieldLocation locRight = state.getFields().registerConstant(Int32.ZERO);
        code.insertTokenCode(ScriptToken.IF);
        code.insertTokenCode(ScriptToken.EQUAL_TO);
        code.insertFieldCode(locLeft);
        code.insertFieldCode(locRight);
        code.insertTokenCode(ScriptToken.BEGIN);
        code.insertCode(initCode);
        code.insertTokenCode(ScriptToken.END);
        code.insertTokenCode(ScriptToken.ELSE);
        code.insertTokenCode(ScriptToken.BEGIN);
        code.insertCode(mainCode);
        code.insertTokenCode(ScriptToken.END);
    }
}
