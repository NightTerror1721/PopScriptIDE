/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import kp.ps.editor.ScriptEditor;
import kp.ps.script.ScriptInternal;
import kp.ps.script.ScriptToken;
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
    
    
    private static void printTokens() throws IOException
    {
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get("tokens.txt"))))
        {
            for(ScriptToken token : ScriptToken.values())
                pw.append("case ").append(token.getLangName()).append(": \"").append(token.getLangName()).println("\";");
        }
    }
    
    private static void printInternals() throws IOException
    {
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get("internals.txt"))))
        {
            for(ScriptInternal internal : ScriptInternal.values())
            {
                String name = parsePrefix(internal.getInternalName());
                pw.append("case ").append(parseName(name)).append(": \"").append(name).println("\";");
            }
        }
    }
    
    private static String parsePrefix(String name)
    {
        if(name.startsWith("M_"))
            name = "MY_" + name.substring(2);
        else if(name.startsWith("B_"))
            name = "BLUE_" + name.substring(2);
        else if(name.startsWith("R_"))
            name = "RED_" + name.substring(2);
        else if(name.startsWith("Y_"))
            name = "YELLOW_" + name.substring(2);
        else if(name.startsWith("G_"))
            name = "GREEN_" + name.substring(2);
        
        return name;
    }
    
    private static String parseName(String name)
    {
        StringBuilder sb = new StringBuilder(name.length());
        char[] chars = name.toCharArray();
        for(int i = 0; i < chars.length; ++i)
        {
            if(i == 0 || chars[i - 1] == '_' && Character.isAlphabetic(chars[i]))
                sb.append(chars[i]);
            else if(chars[i] == '_');
            else sb.append(Character.toLowerCase(chars[i]));
        }
        return sb.toString();
    }
}
