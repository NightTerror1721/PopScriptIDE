/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.decompiler;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import kp.ps.script.Script;
import kp.ps.script.ScriptToken;
import kp.ps.script.compiler.TypedValue;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.utils.ints.UInt16;

/**
 *
 * @author Marc
 */
public class DecompilerState extends PrintWriter
{
    private final Script script;
    private final FieldManager fields;
    private final Iterator<UInt16> codes;
    
    public DecompilerState(Script script, OutputStream output)
    {
        super(output);
        this.script = Objects.requireNonNull(script);
        this.fields = new FieldManager(script);
        this.codes = script.codesIterator();
    }
    
    public final Script getScript() { return script; }
    public final FieldManager getFields() { return fields; }
    
    public final void printVariables(int identation)
    {
        fields.printVars(this, identation);
    }
    
    public final boolean hasMoreCodes() { return codes.hasNext(); }
    
    private ScriptToken nextToken() throws DecompilerException
    {
        if(!codes.hasNext())
            throw new DecompilerException("Unexpected end of file.");
        
        UInt16 code = codes.next();
        ScriptToken token = ScriptToken.fromCode(code);
        if(token == null)
            throw new DecompilerException("Invalid token value '%h'", code.toInt());
        
        return token;
    }
    
    public final ScriptToken nextCommand(ScriptToken... possibleValues) throws DecompilerException
    {
        ScriptToken token = nextToken();
        if(!token.isCommand() && !token.useCommandNumber())
        {
            if(possibleValues != null && possibleValues.length > 0)
            {
                for(int i = 0; i < possibleValues.length; ++i)
                    if(token == possibleValues[i])
                        return token;
                
                String[] tokenCodes = Stream.of(possibleValues)
                        .map(t -> Integer.toHexString(t.getCode().toInt()))
                        .toArray(String[]::new);
                throw new DecompilerException("Unexpected command token '%h'. Possible values: %s.", token.getCode().toInt(), tokenCodes);
            }
            
            return token;
        }
        
        if(possibleValues != null && possibleValues.length > 0)
        {
            String[] tokenCodes = Stream.of(possibleValues)
                    .map(t -> Integer.toHexString(t.getCode().toInt()))
                    .toArray(String[]::new);
            throw new DecompilerException("Unexpected command token '%h'. Possible values: %s.", token.getCode().toInt(), tokenCodes);
        }
        throw new DecompilerException("Unexpected token '%h'. Expected valid command token.", token.getCode().toInt());
    }
    
    public final ScriptToken checkNextCommand(ScriptToken command) throws DecompilerException
    {
        return nextCommand(command);
    }
    
    public final ScriptToken checkNextCommand(ScriptToken... possibleComands) throws DecompilerException
    {
        if(possibleComands == null || possibleComands.length < 1)
            throw new IllegalStateException();
        return nextCommand(possibleComands);
    }
    
    public final TypedValue nextTypedValue(TypeId type) throws DecompilerException
    {
        ScriptToken token = nextToken();
        TypedValue value = TypedValue.from(token);
        if(value == null)
            throw new DecompilerException("Unexpected token '%h'. Expected valid %s token.", token.getCode().toInt(), type.getTypeName());
        return value;
    }
    
    public final TypedValue nextAction() throws DecompilerException
    {
        return nextTypedValue(TypeId.ACTION);
    }
    
    public final Field nextField() throws DecompilerException
    {
        if(!codes.hasNext())
            throw new DecompilerException("Unexpected end of file.");
        
        return fields.getField(codes.next().toInt());
    }
    
    public final Field nextEverySecondParam() throws DecompilerException
    {
        if(!codes.hasNext())
            throw new DecompilerException("Unexpected end of file.");
        
        UInt16 code = codes.next();
        ScriptToken token = ScriptToken.fromCode(code);
        if(token == ScriptToken.BEGIN)
            return null;
        
        return fields.getField(code.toInt());
    }
}
