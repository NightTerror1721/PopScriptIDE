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
public final class Int32 extends AbstractInt<Int32>
{
    public static final Int32 ZERO = new Int32(0);
    public static final Int32 ONE = new Int32(1);
    public static final Int32 MINUSONE = new Int32(-1);
    
    public static final Int32 MAX = new Int32(Integer.MAX_VALUE);
    public static final Int32 MIN = new Int32(Integer.MIN_VALUE);
    
    public static final int BYTES = Integer.BYTES;
    
    public Int32(int value) { super(value); }
    
    
    @Override
    protected final Int32 instance(int value) { return new Int32(value); }
    
    
    public static final Int32 valueOf(byte value) { return new Int32(value); }
    public static final Int32 valueOf(short value) { return new Int32(value); }
    public static final Int32 valueOf(int value) { return new Int32(value); }
    public static final Int32 valueOf(long value) { return new Int32((int) value); }
    public static final Int32 valueOf(IntegerValue<?> value) { return new Int32(value.toInt()); }
    
    
    public static final void write(byte[] buffer, int offset, Int32 value) { IOUtils.writeSignedInt32(buffer, offset, value.value); }
    public static final Int32 read(byte[] buffer, int offset) { return new Int32(IOUtils.readSignedInt32(buffer, offset)); }
    
    public static final void write(OutputStream os, Int32 value) throws IOException { IOUtils.writeSignedInt32(os, value.value); }
    public static final Int32 read(InputStream is, int offset) throws IOException { return new Int32(IOUtils.readSignedInt32(is)); }
}
