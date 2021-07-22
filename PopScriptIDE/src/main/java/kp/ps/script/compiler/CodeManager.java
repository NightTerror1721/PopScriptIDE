/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.compiler;

import java.util.LinkedList;
import kp.ps.script.Script;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.FieldsManager.FieldLocation;
import kp.ps.utils.ints.UInt16;

/**
 *
 * @author Marc
 */
public class CodeManager
{
    private final LinkedList<UInt16> bytecode = new LinkedList<>();
    
    private void insertCode(UInt16 code) throws CompilerException
    {
        if(bytecode.size() >= Script.MAX_CODES)
            throw new CompilerException("Max bytecode data exceded.");
        bytecode.add(code);
    }
    
    public final void insertFieldCode(FieldLocation field) throws CompilerException
    {
        insertCode(UInt16.valueOf(field.getLocation()));
    }
    
    public final void insertTokenCode(ScriptToken token) throws CompilerException
    {
        insertCode(token.getCode());
    }
    
    public final void insertToScript(Script script)
    {
        script.insertCodes(bytecode);
    }
}
