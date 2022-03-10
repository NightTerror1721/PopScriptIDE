/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script.decompiler;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import kp.ps.script.Script;
import kp.ps.script.ScriptField;
import kp.ps.script.compiler.types.TypeId;
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public class FieldManager implements Iterable<Field>
{
    private final Field[] fields = new Field[Script.MAX_FIELDS];
    
    public FieldManager(Script script)
    {
        int index = 0;
        for(ScriptField sfield : script.fieldsIterable())
        {
            if(sfield != null && !sfield.isInvalid())
                fields[index] = Field.valueOf(index, sfield);
            index++;
        }
    }
    
    public final void printVars(PrintWriter output, int identation)
    {
        String sident = Utils.stringDup(' ', identation);
        for(Field field : fields)
            if(field != null && field.isUser())
            {
                output.append(sident)
                        .append(TypeId.INT.getTypeName())
                        .append(' ')
                        .append(field.toString())
                        .append(';')
                        .println();
            }
    }
    
    public final Field getField(int index) throws DecompilerException
    {
        if(index < 0 || index >= fields.length)
            throw new DecompilerException("Invalid field index '%s'.", index);
        
        if(fields[index] == null || fields[index].isInvalid())
            throw new DecompilerException("Uninitiated field with index = '%s'.", index);
        
        return fields[index];
    }
    
    public final Stream<Field> stream()
    {
        return Stream.of(fields);
    }

    @Override
    public final Iterator<Field> iterator()
    {
        return new Iterator<Field>()
        {
            private int idx = 0;
            
            @Override
            public boolean hasNext() { return idx < fields.length; }

            @Override
            public Field next()
            {
                if(idx >= fields.length)
                    throw new NoSuchElementException();
                return fields[idx++];
            }
        };
    }
}
