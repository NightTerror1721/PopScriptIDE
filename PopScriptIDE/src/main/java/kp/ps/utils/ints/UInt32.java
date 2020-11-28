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
import kp.ps.utils.Utils;

/**
 *
 * @author Marc
 */
public final class UInt32 extends AbstractLong<UInt32>
{
    public static final UInt32 ZERO = new UInt32(0);
    public static final UInt32 ONE = new UInt32(1);
    
    public static final UInt32 MAX = new UInt32(0xffffffffL);
    public static final UInt32 MIN = new UInt32(0);
    
    public static final int BYTES = Integer.BYTES;
    
    public UInt32(long value) { super(Utils.clamp(value, 0L, 0xffffffffL)); }
    
    
    @Override
    protected final UInt32 instance(long value) { return new UInt32(value); }
    
    
    public static final UInt32 valueOf(byte value) { return new UInt32(value); }
    public static final UInt32 valueOf(short value) { return new UInt32(value); }
    public static final UInt32 valueOf(int value) { return new UInt32(value); }
    public static final UInt32 valueOf(long value) { return new UInt32(value); }
    public static final UInt32 valueOf(IntegerValue<?> value) { return new UInt32(value.toLong()); }
    
    
    public static final void write(byte[] buffer, int offset, UInt32 value) { IOUtils.writeUnsignedInt32(buffer, offset, value.value); }
    public static final UInt32 read(byte[] buffer, int offset) { return new UInt32(IOUtils.readUnsignedInt32(buffer, offset)); }
    
    public static final void write(OutputStream os, UInt32 value) throws IOException { IOUtils.writeUnsignedInt32(os, value.value); }
    public static final UInt32 read(InputStream is, int offset) throws IOException { return new UInt32(IOUtils.readUnsignedInt32(is)); }
}
