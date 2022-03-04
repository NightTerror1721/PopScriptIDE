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
import kp.ps.editor.completion.PopScriptNamespaceCompletionProvider;
import kp.ps.editor.highlight.HighlightNamespace;
import kp.ps.script.Script;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.FieldsManager.FieldLocation;
import kp.ps.script.instruction.Instruction;
import kp.ps.script.instruction.InstructionCompiler;
import kp.ps.script.instruction.InstructionParser;
import kp.ps.utils.CodeReader;
import kp.ps.utils.Pointer;
import kp.ps.utils.ints.Int32;

/**
 *
 * @author Marc
 */
public class ScriptCompiler
{
    private ScriptCompiler() {}
    
    public static final Script compile(
            Path path,
            ErrorList errors,
            PopScriptNamespaceCompletionProvider completionProvider,
            Pointer<HighlightNamespace> highlights) throws IOException
    {
        try(InputStream input = Files.newInputStream(path))
        {
            return compile(new CodeReader(input), errors, path, completionProvider, highlights);
        }
    }
    public static final Script compile(Path path, ErrorList errors) throws IOException
    {
        return compile(path, errors, null, null);
    }
    
    public static final Script compile(
            String code,
            ErrorList errors,
            Path fakeSource,
            PopScriptNamespaceCompletionProvider completionProvider,
            Pointer<HighlightNamespace> highlights)
    {
        return compile(new CodeReader(code), errors, fakeSource, completionProvider, highlights);
    }
    public static final Script compile(String code, ErrorList errors, Path fakeSource)
    {
        return compile(new CodeReader(code), errors, fakeSource, null, null);
    }
    public static final Script compile(String code, ErrorList errors)
    {
        return compile(new CodeReader(code), errors, null, null, null);
    }
    
    public static final Script compile(
            InputStream input,
            ErrorList errors,
            Path fakeSource,
            PopScriptNamespaceCompletionProvider completionProvider,
            Pointer<HighlightNamespace> highlights)
    {
        return compile(new CodeReader(input), errors, fakeSource, completionProvider, highlights);
    }
    public static final Script compile(InputStream input, ErrorList errors, Path fakeSource)
    {
        return compile(new CodeReader(input), errors, fakeSource, null, null);
    }
    public static final Script compile(InputStream input, ErrorList errors)
    {
        return compile(new CodeReader(input), errors, null, null, null);
    }
    
    private static Script compile(
            CodeReader source,
            ErrorList errors,
            Path mainSource,
            PopScriptNamespaceCompletionProvider completionProvider,
            Pointer<HighlightNamespace> highlights)
    {
        if(errors == null)
            errors = new ErrorList();
        
        if(mainSource != null)
            mainSource = mainSource.toAbsolutePath();
        
        CompilerState state = new CompilerState(errors);
        CodeManager initCode = new CodeManager();
        CodeManager mainCode = new CodeManager();
        
        try
        {
            if(mainSource != null)
                state.pushSourceFile(mainSource);
        }
        catch(CompilerException ex) { throw new IllegalStateException(ex); }
        
        List<Instruction> insts = InstructionParser.parse(state, source, false, errors);
        InstructionCompiler.staticCompile(state, initCode, mainCode, insts);
        
        if(mainSource != null)
            state.popSourceFile();
        
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
            errors.addError(mainSource, 0, source.getCurrentLine(), ex);
        }
        
        Script script = new Script();
        state.getFields().insertToScript(script);
        code.insertToScript(script);
        
        if(completionProvider != null)
            completionProvider.fill(state.getNamespace());
        
        if(highlights != null)
            highlights.set(new HighlightNamespace(state.getNamespace()));
        
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
        code.insertTokenCode(ScriptToken.ENDIF);
    }
}
