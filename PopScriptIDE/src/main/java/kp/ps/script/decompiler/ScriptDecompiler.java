/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.decompiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import kp.ps.script.Script;

/**
 *
 * @author Marc
 */
public final class ScriptDecompiler
{
    private ScriptDecompiler() {}
    
    public static final void decompile(OutputStream output, Script script)
    {
        ScriptDecompilerImpl.decompile(script, output);
    }
    
    public static final void decompile(OutputStream output, InputStream scriptInput) throws IOException
    {
        Script script = new Script();
        script.read(scriptInput);
        ScriptDecompilerImpl.decompile(script, output);
        script.clear();
    }
    
    public static final void decompile(OutputStream output, String scriptInput) throws IOException
    {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(scriptInput.getBytes("utf8")))
        {
            decompile(output, bais);
        }
    }
    
    public static final void decompile(OutputStream output, Path scriptFile) throws IOException
    {
        try(InputStream is = Files.newInputStream(scriptFile))
        {
            decompile(output, is);
        }
    }
    
    
    public static final String decompile(Script script)
    {
        String code = "";
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ScriptDecompilerImpl.decompile(script, baos);
            code = baos.toString();
        }
        catch(IOException ex) { ex.printStackTrace(System.err); }
        return code;
    }
    
    public static final String decompile(InputStream scriptInput) throws IOException
    {
        Script script = new Script();
        script.read(scriptInput);
        String code = decompile(script);
        script.clear();
        return code;
    }
    
    public static final String decompile(String scriptInput) throws IOException
    {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(scriptInput.getBytes("utf8")))
        {
            return decompile(bais);
        }
    }
    
    public static final String decompile(Path scriptFile) throws IOException
    {
        try(InputStream is = Files.newInputStream(scriptFile))
        {
            return decompile(is);
        }
    }
}
