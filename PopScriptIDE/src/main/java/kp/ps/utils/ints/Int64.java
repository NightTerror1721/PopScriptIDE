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
public final class Int64 extends AbstractLong<Int64>
{
    public static final Int64 ZERO = new Int64(0);
    public static final Int64 ONE = new Int64(1);
    public static final Int64 MINUSONE = new Int64(-1);
    
    public static final Int64 MAX = new Int64(Long.MAX_VALUE);
    public static final Int64 MIN = new Int64(Long.MIN_VALUE);
    
    public static final int BYTES = Long.BYTES;
    
    public Int64(long value) { super(value); }
    
    
    @Override
    protected final Int64 instance(long value) { return new Int64(value); }
    
    
    public static final Int64 valueOf(byte value) { return new Int64(value); }
    public static final Int64 valueOf(short value) { return new Int64(value); }
    public static final Int64 valueOf(int value) { return new Int64(value); }
    public static final Int64 valueOf(long value) { return new Int64(value); }
    public static final Int64 valueOf(IntegerValue<?> value) { return new Int64(value.toLong()); }
    
    
    public static final void write(byte[] buffer, int offset, Int64 value) { IOUtils.writeSignedInt64(buffer, offset, value.value); }
    public static final Int64 read(byte[] buffer, int offset) { return new Int64(IOUtils.readSignedInt64(buffer, offset)); }
    
    public static final void write(OutputStream os, Int64 value) throws IOException { IOUtils.writeSignedInt64(os, value.value); }
    public static final Int64 read(InputStream is, int offset) throws IOException { return new Int64(IOUtils.readSignedInt64(is)); }
}
