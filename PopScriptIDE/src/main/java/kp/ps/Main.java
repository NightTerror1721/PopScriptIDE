/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import kp.ps.script.Script;
import kp.ps.script.compiler.ErrorList;
import kp.ps.script.compiler.functions.InnerFunction;
import kp.ps.script.compiler.functions.InnerFunctionPool;

/**
 *
 * @author Marc
 */
public class Main
{
    public static void main(String[] args) throws IOException
    {
        List<InnerFunction> actions = InnerFunctionPool.list();
        for(InnerFunction func : actions)
            System.out.println(func);
        
        ErrorList errors = new ErrorList();
        Script script = kp.ps.script.compiler.ScriptCompiler.compile(Path.of("test.kps"), errors);
        
        System.out.println("\nErrors:");
        for(ErrorList.ErrorEntry error : errors)
        {
            error.getCause().printStackTrace(System.out);
            //System.out.println("Error ar " + error.getStartLine() + ": " + error.getCause().getMessage());
        }
        
        if(!errors.hasErrors())
        {
            script.write(Path.of("test.dat"));
            
            script.clear();
            script.read(Path.of("test.dat"));
        }
    }
}
