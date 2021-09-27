/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import java.io.IOException;
import kp.ps.editor.ScriptEditor;
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public class Main
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        LafManager.install(new DarculaTheme());
        Utils.initPopScriptLanguage();
        ScriptEditor.open();
        
        /*List<InnerFunction> actions = InnerFunctionPool.list();
        for(InnerFunction func : actions)
            System.out.println(func);*/
        
        /*ErrorList errors = new ErrorList();
        Script script = ScriptCompiler.compile(Path.of("test.kps"), errors);
        
        System.out.println("\nErrors:");
        for(ErrorList.ErrorEntry error : errors)
        {
            error.getCause().printStackTrace(System.out);
            //System.out.println("Error ar " + error.getStartLine() + ": " + error.getCause().getMessage());
        }*/
        
        /*if(!errors.hasErrors())
        {
            script.write(Path.of("test.dat"));
            
            script.clear();
            script.read(Path.of("test.dat"));
        }*/
        
        //Utils.generateFunctionsFile(Paths.get("functions.txt"));
        //Utils.generateInternalAndTypeValuesFile(Paths.get("elements.txt"));
        //org.fife.tmm.Main.main(args);
        
        
    }
}
