/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import kp.ps.utils.IOUtils;
import kp.ps.utils.RawData;
import kp.ps.utils.ints.Int8;
import kp.ps.utils.ints.UInt16;

/**
 *
 * @author Marc
 */
public final class Script
{
    public static final int MAX_CODES = 4095;
    public static final int MAX_FIELDS = 512;
    public static final int SCRIPT_VERSION = 12;
    public static final int MAX_VARS = 64;
    
    static final int NO_COMMANDS = 27;
    static final int TOKEN_OFFSET = 1000;
    static final int INT_OFFSET = 1000;
    
    private final RawData codes = new RawData(UInt16.BYTES * MAX_CODES);
    private final ScriptField[] fields = new ScriptField[MAX_FIELDS];
    
    public Script()
    {
        for(int i = 0; i < fields.length; ++i)
            fields[i] = ScriptField.INVALID;
    }
    
    public final void clear()
    {
        codes.fill(0);
        for(int i = 0; i < fields.length; ++i)
            fields[i] = ScriptField.INVALID;
    }
    
    
    private void checkCodeBounds(int index)
    {
        if(index < 0 || index >= MAX_CODES)
            throw new IndexOutOfBoundsException();
    }
    
    public final void setCode(int index, UInt16 code)
    {
        checkCodeBounds(index);
        codes.writeUnsignedInt16(index * UInt16.BYTES, code);
    }
    
    public final UInt16 getCode(int index)
    {
        checkCodeBounds(index);
        return codes.readUnsignedInt16(index * UInt16.BYTES);
    }
    
    public final void copyCodesFrom(Script other)
    {
        codes.copyFrom(other.codes);
    }
    
    public final void insertCodes(int index, UInt16[] codes, int off, int len)
    {
        for(int i = off; i < len; ++i)
        {
            if(index >= MAX_CODES)
                break;
            
            this.codes.writeUnsignedInt16((index++) * UInt16.BYTES, codes[i]);
        }
    }
    public final void insertCodes(int index, UInt16[] codes) { insertCodes(index, codes, 0, codes.length); }
    public final void insertCodes(int index, Iterable<UInt16> codes)
    {
        for(UInt16 code : codes)
        {
            if(index >= MAX_CODES)
                break;
            
            this.codes.writeUnsignedInt16((index++) * UInt16.BYTES, code);
        }
    }
    public final void insertCodes(Iterable<UInt16> codes) { insertCodes(0, codes); }
    
    public final void insertCodes(int index, Iterator<UInt16> codes) { insertCodes(index, () -> codes); }
    public final void insertCodes(Iterator<UInt16> codes) { insertCodes(0, () -> codes); }
    
    public final Iterator<UInt16> codesIterator() { return new CodesIterator(); }
    public final Iterable<UInt16> codesIterable() { return CodesIterator::new; }
    public final void forEachCode(BiConsumer<UInt16, Integer> action)
    {
        for(CodesIterator it = new CodesIterator(); it.hasNext();)
        {
            int index = it.it;
            UInt16 code = it.next();
            action.accept(code, index);
        }
    }
    
    
    private void checkFieldBounds(int index)
    {
        if(index < 0 || index >= MAX_FIELDS)
            throw new IndexOutOfBoundsException();
    }
    
    public final void setField(int index, ScriptField field)
    {
        checkFieldBounds(index);
        fields[index] = Objects.requireNonNullElse(field, ScriptField.INVALID);
    }
    
    public final ScriptField getField(int index)
    {
        checkFieldBounds(index);
        return Objects.requireNonNullElse(fields[index], ScriptField.INVALID);
    }
    
    public final void insertFields(int index, ScriptField[] fields, int off, int len)
    {
        for(int i = off; i < len; ++i)
        {
            if(index >= this.fields.length)
                break;
            
            this.fields[index++] = Objects.requireNonNullElse(fields[i], ScriptField.INVALID);
        }
    }
    public final void insertFields(int index, ScriptField[] fields) { insertFields(index, fields, 0, fields.length); }
    public final void insertFields(int index, Iterable<ScriptField> fields)
    {
        for(ScriptField field : fields)
        {
            if(index >= this.fields.length)
                break;
            
            this.fields[index++] = Objects.requireNonNullElse(field, ScriptField.INVALID);
        }
    }
    public final void insertFields(Iterable<ScriptField> fields) { insertFields(0, fields); }
    
    public final void insertFields(int index, Iterator<ScriptField> fields) { insertFields(index, () -> fields); }
    public final void insertFields(Iterator<ScriptField> fields) { insertFields(0, () -> fields); }
    
    public final Iterator<ScriptField> fieldsIterator() { return new FieldsIterator(); }
    public final Iterable<ScriptField> fieldsIterable() { return FieldsIterator::new; }
    public final void forEachField(BiConsumer<ScriptField, Integer> action)
    {
        for(FieldsIterator it = new FieldsIterator(); it.hasNext();)
        {
            int index = it.it;
            ScriptField field = it.next();
            action.accept(Objects.requireNonNullElse(field, ScriptField.INVALID), index);
        }
    }
    
    
    
    public final void read(InputStream is) throws IOException
    {
        IOUtils.readUnsignedInt16(is); //Skip version
        codes.readFrom(is);
        
        byte[] rawFields = new byte[ScriptField.BYTES * MAX_FIELDS];
        IOUtils.readFully(is, rawFields);
        for(int i = 0; i < fields.length; ++i)
            fields[i] = ScriptField.read(rawFields, i * ScriptField.BYTES);
    }
    public final void read(Path path) throws IOException
    {
        try(InputStream is = Files.newInputStream(path))
        {
            read(is);
        }
    }
    public final void read(File file) throws IOException { read(file.toPath()); }
    
    public final void write(OutputStream os) throws IOException
    {
        Int8.write(os, Int8.valueOf(12));
        Int8.write(os, Int8.ZERO);
        codes.writeTo(os);
        
        byte[] rawFields = new byte[ScriptField.BYTES * MAX_FIELDS];
        for(int i = 0; i < fields.length; ++i)
            ScriptField.write(rawFields, i * ScriptField.BYTES, fields[i]);
        os.write(rawFields);
        
        os.write(new byte[264]);
    }
    public final void write(Path path) throws IOException
    {
        try(OutputStream os = Files.newOutputStream(path))
        {
            write(os);
        }
    }
    public final void write(File file) throws IOException { write(file.toPath()); }
    
    
    private final class CodesIterator implements Iterator<UInt16>
    {
        private int it = 0;

        @Override
        public final boolean hasNext() { return it < MAX_CODES; }

        @Override
        public final UInt16 next()
        {
            if(it >= MAX_CODES)
                throw new NoSuchElementException();
            
            return codes.readUnsignedInt16((it++) * UInt16.BYTES);
        }
    }
    
    private final class FieldsIterator implements Iterator<ScriptField>
    {
        private int it = 0;
        
        @Override
        public final boolean hasNext() { return it < fields.length; }

        @Override
        public final ScriptField next()
        {
            if(it >= fields.length)
                throw new NoSuchElementException();
            
            return Objects.requireNonNullElse(fields[it++], ScriptField.INVALID);
        }
        
    }
}
