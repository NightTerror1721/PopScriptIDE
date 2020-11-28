/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kp.ps.utils.ints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import kp.ps.utils.IOUtils;

/**
 *
 * @author Marc
 */
public final class Int16 extends AbstractShort<Int16>
{
    public static final Int16 ZERO = new Int16((short) 0);
    public static final Int16 ONE = new Int16((short) 1);
    public static final Int16 MINUSONE = new Int16((short) -1);
    
    public static final Int16 MAX = new Int16(Short.MAX_VALUE);
    public static final Int16 MIN = new Int16(Short.MIN_VALUE);
    
    public static final int BYTES = Short.BYTES;
    
    public Int16(short value) { super(value); }
    
    
    @Override
    protected final Int16 instance(short value) { return new Int16(value); }
    
    
    public static final Int16 valueOf(byte value) { return new Int16(value); }
    public static final Int16 valueOf(short value) { return new Int16(value); }
    public static final Int16 valueOf(int value) { return new Int16((short) value); }
    public static final Int16 valueOf(long value) { return new Int16((short) value); }
    public static final Int16 valueOf(IntegerValue<?> value) { return new Int16(value.toShort()); }
    
    
    public static final void write(byte[] buffer, int offset, Int16 value) { IOUtils.writeSignedInt16(buffer, offset, value.value); }
    public static final Int16 read(byte[] buffer, int offset) { return new Int16(IOUtils.readSignedInt16(buffer, offset)); }
    
    public static final void write(OutputStream os, Int16 value) throws IOException { IOUtils.writeSignedInt16(os, value.value); }
    public static final Int16 read(InputStream is, int offset) throws IOException { return new Int16(IOUtils.readSignedInt16(is)); }
}
